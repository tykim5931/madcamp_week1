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
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.provider.ContactsContract
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    companion object {
        const val SHRUNKEN_MENU = 1
        const val EXPANDED_MENU = 2
        const val DELETE_MENU = 3

    }
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

    // Animation variables & switch
    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}

    private var menuStatus: Int = SHRUNKEN_MENU

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println()
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchView.setOnQueryTextListener(searchViewTextListener) //adapting filter to madapter!

        // get phone from json file
        phoneList = PhoneBookSource(requireContext()).loadPhoneBook() as MutableList<Phone>
        mAdapter = PhoneAdapter(phoneList)
        binding.recycler.adapter = mAdapter
        binding.recycler.layoutManager = LinearLayoutManager(context)
        menuStatus = SHRUNKEN_MENU
        setVisibility(menuStatus)
        setAnimation(menuStatus)
        setClickable(menuStatus)
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
                    val id = cursor.getString(0)
                    val name = cursor.getString(1)
                    val number = cursor.getString(2)
                    // add to phoneList
                    val phone = Phone(id, name, number) // 개별 전화번호 데이터
                    if(id !in phoneList.map{it.id}){    // 만약에 아이디가 다르면 새로 추가
                        phoneList.add(phone) // 결과목록에 추가
                    }
                }
                cursor.close()
            }
            phoneList.sortBy { it.name }
            PhoneBookSource(requireContext()).savePhoneBook(phoneList)
            mAdapter = PhoneAdapter(phoneList)
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
        }

        //On-click listeners
        binding.moreButton.setOnClickListener{
            menuStatus = EXPANDED_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.closeButton.setOnClickListener{
            menuStatus = SHRUNKEN_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }
        // Contact plus button
        binding.addButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requestLauncher.launch(intent)
        }

        binding.selectButton.setOnClickListener{
            mAdapter.updateCB(1)    // 체크박스 모두노출
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
            menuStatus = DELETE_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.cancelButton.setOnClickListener{
            mAdapter.checkBoxList.map{it.checked = false}//체크박스 모두해제
            mAdapter.updateCB(0)    // 체크박스 숨기기
            binding.recycler.adapter = mAdapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
            menuStatus = EXPANDED_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.deleteButton.setOnClickListener{
            val checklist : List<CheckBoxData> = mAdapter.checkBoxList.filter{it.checked}
            if(!checklist.isEmpty() && !phoneList.isEmpty()){
                for (item in checklist){
                    val idx : Int = phoneList.map{it.id}.indexOf(item.id)
                    if(idx != -1) {
                        phoneList.removeAt(idx) // 목록에서 삭제
                    }
                }
                PhoneBookSource(requireContext()).savePhoneBook(phoneList)
                mAdapter = PhoneAdapter(phoneList)
                mAdapter.updateCB(0)    // 체크박스유지
                binding.recycler.adapter = mAdapter
                binding.recycler.layoutManager = LinearLayoutManager(context)
                menuStatus = SHRUNKEN_MENU
                setVisibility(menuStatus)
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateClose)}
                setClickable(menuStatus)
            }
        }


    }

    private fun setVisibility(menuStatus: Int) {
        val allSet = setOf(binding.moreButton, binding.selectButton, binding.addButton, binding.closeButton, binding.deleteButton, binding.cancelButton)
        when(menuStatus) {
            SHRUNKEN_MENU -> {
                val visibleSet = setOf(binding.moreButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
            EXPANDED_MENU -> {
                val visibleSet = setOf(binding.selectButton, binding.addButton, binding.closeButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
            //DELETE_MENU
            else -> {
                val visibleSet = setOf(binding.deleteButton, binding.cancelButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
        }
    }
    private fun setAnimation(menuStatus: Int) {
        when(menuStatus) {
            SHRUNKEN_MENU -> {
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateClose)}
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(toBottom)}
            }
            EXPANDED_MENU -> {
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateOpen)}
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(fromBottom)}
            }
            //DELETE_MENU
            else -> {
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(toBottom)}
            }
        }
    }

    private fun setClickable(menuStatus: Int) {
        val allSet = setOf(binding.moreButton, binding.selectButton, binding.addButton, binding.closeButton, binding.deleteButton, binding.cancelButton)
        when(menuStatus) {
            SHRUNKEN_MENU -> {
                val clickableSet = setOf(binding.moreButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
            EXPANDED_MENU -> {
                val clickableSet = setOf(binding.selectButton, binding.addButton, binding.closeButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
            //DELETE_MENU
            else -> {
                val clickableSet = setOf(binding.deleteButton, binding.cancelButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
        }
    }

}