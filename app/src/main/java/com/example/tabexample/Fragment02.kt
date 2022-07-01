package com.example.tabexample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonJson
import com.example.tabexample.adapter.GalleryAdapter
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.databinding.FragmentGalleryBinding
import com.example.tabexample.model.GalleryImage
import org.json.JSONArray
import org.json.JSONObject

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
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            // Do something else with the URI. E.g, save the URI as a string in the database
            val json = JSONArray()
            for (item in galleryDataset)
            {
                val jsonObject = JSONObject()
                jsonObject.put("imageUriString", item.imageUriString)
                json.put(jsonObject)
            }
            val fileName = "images.json"
            val jsonObject = JSONObject()
            jsonObject.put("imageUriString", uri.toString())
            json.put(jsonObject)
            requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toString().toByteArray())
            }
            galleryDataset = GalleryDatasource(requireContext()).loadGallery()
            val recyclerView = binding.recyclerView
            recyclerView.adapter = GalleryAdapter(requireContext(), galleryDataset)
        }
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