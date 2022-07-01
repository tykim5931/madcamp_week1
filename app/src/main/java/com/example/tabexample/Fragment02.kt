package com.example.tabexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tabexample.adapter.GalleryAdapter
import com.example.tabexample.data.GalleryDatasource
import com.example.tabexample.databinding.FragmentGalleryBinding

class Fragment02 : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val galleryDataset = GalleryDatasource().loadGallery()

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
        recyclerView.adapter = GalleryAdapter(requireContext(), galleryDataset)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}