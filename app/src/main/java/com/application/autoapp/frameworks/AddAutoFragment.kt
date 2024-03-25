package com.application.autoapp.frameworks

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.application.autoapp.adapters.DataAutoAdapter
import com.application.autoapp.viewmodels.DataAutoViewModel
import com.application.autoapp.databinding.FragmentListBinding
import com.application.autoapp.databinding.FragmentAddAutoBinding
import com.squareup.picasso.Picasso

class AddAutoFragment(private val bindingFromListFragment: FragmentListBinding, private val viewModel: DataAutoViewModel) : Fragment() {
    private var _binding: FragmentAddAutoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load("https://cdn.onlinewebfonts.com/svg/download_103323.png").into(binding.idAddPhoto)

        viewModel.selectedImageLiveData.observe(viewLifecycleOwner, Observer { imageUri ->
            imageUri?.let {
                Picasso.get().load(imageUri).into(binding.idAddPhoto)
            }
        })

        binding.idAddPhoto.setOnClickListener {
            viewModel.actionImage = 1
            viewModel.loadPhotoFromGallery(requireActivity())
        }

        binding.imageClose.setOnClickListener {
            bindingFromListFragment.floatingActionButton.visibility = View.VISIBLE
            DataAutoAdapter.opened = false
            viewModel.selectedImageLiveData.value = Uri.parse("https://cdn.onlinewebfonts.com/svg/download_103323.png")
            parentFragmentManager.popBackStack()
        }

        binding.buttonAdd.setOnClickListener {
            val textName = binding.editTextName.editText?.text.toString()
            val textImage = viewModel.getImageUri()
            val textDrive = binding.editTextDrive.editText?.text.toString()
            val textColor = binding.editTextColor.editText?.text.toString()
            val textBody = binding.editTextBody.editText?.text.toString()

            if (textName.isNotEmpty() && textImage.isNotEmpty() && textDrive.isNotEmpty() && textColor.isNotEmpty() && textBody.isNotEmpty()) {

                viewModel.addAuto(textName, textImage, textDrive, textColor, textBody)
                bindingFromListFragment.floatingActionButton.visibility = View.VISIBLE
                DataAutoAdapter.opened = false
                viewModel.selectedImageLiveData.value = Uri.parse("https://cdn.onlinewebfonts.com/svg/download_103323.png")
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}