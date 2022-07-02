package com.example.tabexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tabexample.adapter.GalleryAdapter
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.databinding.FragmentGalleryBinding
import com.example.tabexample.model.GalleryImage
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        galleryDataset = GalleryDatasource(requireContext()).loadGallery()
        recyclerView.adapter = GalleryAdapter(requireContext(), galleryDataset)
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}