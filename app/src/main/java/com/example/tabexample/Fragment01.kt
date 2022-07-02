package com.example.tabexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.adapter.GalleryAdapter
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.data.PhoneBookSource
import com.example.tabexample.databinding.ContactItemBinding
import com.example.tabexample.databinding.FragmentContactBinding
import com.example.tabexample.model.Phone
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.typeOf


/* contact Fragment */
class Fragment01 : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private var _binding2: ContactItemBinding? = null
    private val binding2 get() = _binding2!!

    lateinit var mAdapter:PhoneAdapter
    var phoneList= mutableListOf<Phone>()

    lateinit var requestLauncher: ActivityResultLauncher<Intent>

    var searchViewTextListener: SearchView.OnQueryTextListener =
        object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String?): Boolean = false // 검색버튼 입력시. 검색버튼 없으므로 사용 X
            override fun onQueryTextChange(s: String?): Boolean {
                mAdapter.getFilter().filter(s)
                binding.recycler.adapter = mAdapter
                binding.recycler.layoutManager = LinearLayoutManager(context)
                println("SearchViews Text is changed: $s")
                return false
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PhoneBookSource(requireContext()).savePhoneBook(phoneList)
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchView.setOnQueryTextListener(searchViewTextListener) //adapting filter to madapter!

        // get phone from json file
        phoneList = PhoneBookSource(requireContext()).loadPhoneBook() as MutableList<Phone>
//        phoneList = getPhoneNumbers(searchText) as MutableList<Phone>

        // set read phonelist to adapter
        mAdapter = PhoneAdapter(phoneList)
        binding.recycler.adapter = mAdapter
        binding.recycler.layoutManager = LinearLayoutManager(context)

        // 주소록 열어서 연락처 읽어오기
        requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                val cursor = context?.contentResolver?.query(
                    it.data!!.data!!,
                    arrayOf<String>(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ),
                    null,null,null
                )
                println("Cursor size: ${cursor?.count}")
                if (cursor!!.moveToFirst()){
                    // 만약에 아이디가 다르면 새로 추가
                    val id = cursor.getString(0)
                    val name = cursor.getString(1)
                    val number = cursor.getString(2)
                    // add to phoneList
                    val phone = Phone(id, name, number) // 개별 전화번호 데이터
                    if(id !in phoneList.map{it.id}){
                        phoneList.add(phone) // 결과목록에 추가
                    }
                }
            }
            phoneList.sortBy { it.name }
            mAdapter = PhoneAdapter(phoneList)
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
        }

        binding.contactButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requestLauncher.launch(intent)
        }
    }

}