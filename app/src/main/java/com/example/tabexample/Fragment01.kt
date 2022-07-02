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
import com.example.tabexample.model.CheckBoxData
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
        println()
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        PhoneBookSource(requireContext()).savePhoneBook(phoneList)
//        _binding = null
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchView.setOnQueryTextListener(searchViewTextListener) //adapting filter to madapter!

        // get phone from json file
        phoneList = PhoneBookSource(requireContext()).loadPhoneBook() as MutableList<Phone>
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
            PhoneBookSource(requireContext()).savePhoneBook(phoneList)
            mAdapter = PhoneAdapter(phoneList)
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
        }

        // Contact plus button clicked
        binding.contactButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requestLauncher.launch(intent)
        }
        binding.contactButton.setOnLongClickListener{
            mAdapter.updateCB(1)    // 체크박스 모두노출
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)

            binding.contactButton.visibility = View.GONE // 추가버튼 안 보이게
            binding.deleteButton.visibility = View.VISIBLE // 삭제버튼 보이게
            binding.cancelButton.visibility = View.VISIBLE // 취소버튼 보이게
            true
        }

        binding.cancelButton.setOnClickListener{
            mAdapter.updateCB(0)    // 체크박스 모두해제
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)

            binding.contactButton.visibility = View.VISIBLE // 추가버튼 보이게
            binding.deleteButton.visibility = View.GONE // 삭제버튼 안보이게
            binding.cancelButton.visibility = View.GONE // 취소버튼 안보이게
        }

        binding.deleteButton.setOnClickListener{
            var checklist : List<CheckBoxData> = mAdapter.checkBoxList.filter{it.checked}
            if(!checklist.isEmpty() && !phoneList.isEmpty()){
                for (item in checklist){
                    val idx : Int = phoneList.map{it.id}.indexOf(item.id)
                    if(idx != -1) {
                        phoneList.removeAt(idx) // 목록에서 삭제
                    }
                }
            }

            PhoneBookSource(requireContext()).savePhoneBook(phoneList)
            mAdapter = PhoneAdapter(phoneList)
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
        }
    }

    fun resetViewSettings(phoneList: List<Phone>){
        mAdapter = PhoneAdapter(phoneList)
        binding.recycler.adapter = mAdapter
        binding.recycler.layoutManager = LinearLayoutManager(context)

//        mAdapter.setMyItemClickListener(object : PhoneAdapter.MyItemClickListener{
//            override fun onItemClick(position:Int){
//                phoneList[position].name
//            }
//            override fun onLongClick(position: Int) {
//                // 체크박스 모두노출
//                binding.contactButton.visibility = View.GONE // 추가버튼 안 보이게
//                binding.deleteButton.visibility = View.VISIBLE // 삭제버튼 보이게
//                binding.cancelButton.visibility = View.VISIBLE // 취소버튼 보이게
//            }
//        })
    }

}