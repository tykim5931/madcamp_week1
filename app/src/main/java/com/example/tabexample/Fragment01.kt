package com.example.tabexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.Manifest
import android.provider.ContactsContract
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.databinding.FragmentContactBinding
import com.example.tabexample.databinding.FragmentGalleryBinding
import com.example.tabexample.model.Phone


/* contact Fragment */
class Fragment01 : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    lateinit var mAdapter:PhoneAdapter
    var phoneList= mutableListOf<Phone>()
    var searchText = ""
    var sortText = "asc"

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
        binding.searchView.setOnQueryTextListener(searchViewTextListener) //adapting filter to madapter!

        phoneList = getPhoneNumbers(sortText, searchText) as MutableList<Phone>
        mAdapter = PhoneAdapter(phoneList)
        binding.recycler.adapter = mAdapter
        binding.recycler.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    fun getPhoneNumbers(sort:String, name:String) : List<Phone>{
        val list = mutableListOf<Phone>()
        
        // content resolver로 데이터를 가져옴
        // 주소, 컬럼, 조건
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projections = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID //주소록의 id. 전화번호의 id는 phone._ID
            , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            , ContactsContract.CommonDataKinds.Phone.NUMBER)
        var where:String? = null
        var whereValues:Array<String>? = null
        if(name.isNotEmpty()){
            where = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?" //번호로 검색하려면 Number
            whereValues = arrayOf(name)
        }
        val optionSort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" $sort"

        context?.run{   /*context !null 인 경우 내부동작 실행.*/
            val cursor = contentResolver.query(phoneUri, projections, where, whereValues, optionSort)
            // 반복문으로 아이디, 이름 가져오면서 전화번호 조회 쿼리 한 번 더 돌린다
            while(cursor?.moveToNext() == true){
                val id = cursor?.getString(0)
                val name = cursor?.getString(1)
                var number = cursor?.getString(2)
                val phone = Phone(id, name, number) // 개별 전화번호 데이터
                list.add(phone) // 결과목록에 추가
            }
        }
        return list
    }

}