package com.example.tabexample

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabexample.adapter.GalleryAdapter
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.data.PhoneBookSource
import com.example.tabexample.databinding.FragmentGalleryBinding
import com.example.tabexample.model.CheckBoxData
import com.example.tabexample.model.CheckBoxImg
import com.example.tabexample.model.GalleryImage
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path as Path1

class Fragment02 : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var galleryDataset: List<GalleryImage>

    // Animation variables & switch
    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy{ AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}

    private var menuStatus: Int = Fragment01.SHRUNKEN_MENU

    companion object{
        const val REQUEST_IMAGE_GET = 1
    }
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //  you will get result here in result.data
            val uri = result.data?.data!!
            // Do something else with the URI. E.g, save the URI as a string in the database
            try {
                val dir = File(requireContext().filesDir, "images")
                if(!dir.exists()) {
                    dir.mkdir()
                }
                lateinit var fileName: String
                lateinit var file: File
                do {
                    fileName = uri.hashCode().toString() + getRandomString(10)
                    file = File(dir,fileName)
                }
                while(file.exists())
                val inputStream = requireContext().contentResolver.openInputStream(uri)!!
                val bytes = inputStream.readBytes()
                FileOutputStream(file).use {
                    it.write(bytes)
                    it.close()
                }
                inputStream.close()
                galleryDataset = GalleryDatasource(requireContext()).loadGallery()
                val recyclerView = binding.recyclerView
                recyclerView.adapter = GalleryAdapter(requireContext(), galleryDataset)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun getRandomString(length: Int): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map{ kotlin.random.Random.nextInt(0, charPool.size)}
            .map(charPool::get)
            .joinToString("")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        galleryDataset = GalleryDatasource(requireContext()).loadGallery()

        var mAdapter = GalleryAdapter(requireContext(), galleryDataset)
        recyclerView.adapter = mAdapter
        menuStatus = Fragment01.SHRUNKEN_MENU
        setVisibility(menuStatus)
        setAnimation(menuStatus)
        setClickable(menuStatus)
        //On-click listeners
        binding.moreButton.setOnClickListener{
            menuStatus = Fragment01.EXPANDED_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }
        binding.closeButton.setOnClickListener{
            menuStatus = Fragment01.SHRUNKEN_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
                it.addCategory(Intent.CATEGORY_OPENABLE)
                it.type = "image/*"
                it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pickImage.launch(intent)
        }

        binding.selectButton.setOnClickListener{
            mAdapter = GalleryAdapter(requireContext(), galleryDataset) // update mAdapter
            mAdapter.updateCB(View.VISIBLE)    // 체크박스 모두노출
            recyclerView.adapter = mAdapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

            menuStatus = Fragment01.DELETE_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.cancelButton.setOnClickListener{
            mAdapter = GalleryAdapter(requireContext(), galleryDataset) // update mAdapter
            mAdapter.checkBoxList.map{it.checked = false}//체크박스 모두해제
            mAdapter.updateCB(View.GONE)    // 체크박스 안보이게
            recyclerView.adapter = mAdapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            menuStatus = Fragment01.EXPANDED_MENU
            setVisibility(menuStatus)
            setAnimation(menuStatus)
            setClickable(menuStatus)
        }

        binding.deleteButton.setOnClickListener{
            var checklist : List<CheckBoxData> = mAdapter.checkBoxList.filter{it.checked} // filter checked img
            if(!checklist.isEmpty() && !galleryDataset.isEmpty()){
                for (item in checklist){
                    val dir = File(requireContext().filesDir, "images")
                    val idx : Int = galleryDataset.map{it.id}.indexOf(item.id)
                    if(idx != -1) {
                        val file = File(dir!!,item.id)
                        val deleted = file.delete()
                        println("deleted: "+deleted+ " name: "+ item.id)
                    }
                }
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                galleryDataset = GalleryDatasource(requireContext()).loadGallery()
                mAdapter = GalleryAdapter(requireContext(), galleryDataset) // update mAdapter
                recyclerView.adapter = mAdapter // cast to recyclerView adapter
                menuStatus = Fragment01.SHRUNKEN_MENU
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
            Fragment01.SHRUNKEN_MENU -> {
                val visibleSet = setOf(binding.moreButton)
                visibleSet.forEach{it.visibility = View.VISIBLE}
                allSet.minus(visibleSet).forEach{it.visibility = View.INVISIBLE}
            }
            Fragment01.EXPANDED_MENU -> {
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
            Fragment01.SHRUNKEN_MENU -> {
                listOf(binding.moreButton)
                    .forEach{it.startAnimation(rotateClose)}
                listOf(binding.selectButton, binding.addButton)
                    .forEach{it.startAnimation(toBottom)}
            }
            Fragment01.EXPANDED_MENU -> {
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
            Fragment01.SHRUNKEN_MENU -> {
                val clickableSet = setOf(binding.moreButton)
                clickableSet.forEach{it.isClickable = true}
                allSet.minus(clickableSet).forEach{it.isClickable = false}
            }
            Fragment01.EXPANDED_MENU -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}