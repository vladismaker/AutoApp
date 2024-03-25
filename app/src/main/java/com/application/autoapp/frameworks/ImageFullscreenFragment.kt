package com.application.autoapp.frameworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.application.autoapp.adapters.DataAutoAdapter
import com.application.autoapp.databinding.FragmentImageFullscreenBinding
import com.application.autoapp.databinding.FragmentListBinding
import com.squareup.picasso.Picasso

class ImageFullscreenFragment(private val bindingForList: FragmentListBinding) : Fragment() {
    private var _binding: FragmentImageFullscreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = arguments?.getString("imageUrl")

        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(binding.imageViewFullscreen)
        }

        binding.imageClose.setOnClickListener{
            DataAutoAdapter.opened = false
            DataAutoAdapter.act = false
            bindingForList.floatingActionButton.visibility = View.VISIBLE
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}