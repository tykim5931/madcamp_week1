package com.example.tabexample

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val fab = binding.fab

        fab.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
                it.addCategory(Intent.CATEGORY_OPENABLE)
                it.type = "image/*"
                it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pickImage.launch(intent)
        }

        fab.setOnLongClickListener{
            mAdapter = GalleryAdapter(requireContext(), galleryDataset) // update mAdapter
            mAdapter.updateCB(View.VISIBLE)    // 체크박스 모두노출
            recyclerView.adapter = mAdapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

            fab.visibility = View.GONE // 추가버튼 안 보이게
            binding.deleteButton.visibility = View.VISIBLE // 삭제버튼 보이게
            binding.cancelButton.visibility = View.VISIBLE // 취소버튼 보이게
            true
        }

        binding.cancelButton.setOnClickListener{
            mAdapter = GalleryAdapter(requireContext(), galleryDataset) // update mAdapter
            mAdapter.checkBoxList.map{it.checked = false}//체크박스 모두해제
            mAdapter.updateCB(View.GONE)    // 체크박스 안보이게
            recyclerView.adapter = mAdapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            fab.visibility = View.VISIBLE // 추가버튼 보이게
            binding.deleteButton.visibility = View.GONE // 삭제버튼 안보이게
            binding.cancelButton.visibility = View.GONE // 취소버튼 안보이게
        }

        binding.deleteButton.setOnClickListener{
            var checklist : List<CheckBoxData> = mAdapter.checkBoxList.filter{it.checked} // filter checked img
            if(!checklist.isEmpty() && !galleryDataset.isEmpty()){
                for (item in checklist){
                    val dir = File(requireContext().filesDir, "images")
                    val idx : Int = galleryDataset.map{it.id}.indexOf(item.id)
                    if(idx != -1) {
                        val file = File(dir,item.id)
                        val deleted = file.delete()
                        println("deleted: "+deleted+ " name: "+ item.id)
                    }
                }
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                galleryDataset = GalleryDatasource(requireContext()).loadGallery()
                mAdapter = GalleryAdapter(requireContext(), galleryDataset) // update mAdapter
                recyclerView.adapter = mAdapter // cast to recyclerView adapter
                fab.visibility = View.VISIBLE // 추가버튼 보이게
                binding.deleteButton.visibility = View.GONE // 삭제버튼 안보이게
                binding.cancelButton.visibility = View.GONE // 취소버튼 안보이게
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}