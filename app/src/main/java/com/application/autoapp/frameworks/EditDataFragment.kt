package com.application.autoapp.frameworks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.application.autoapp.adapters.DataAutoAdapter
import com.application.autoapp.viewmodels.DataAutoViewModel
import com.application.autoapp.databinding.FragmentEditDataBinding
import com.application.autoapp.databinding.FragmentListBinding

class EditDataFragment(private val bindingForList: FragmentListBinding, private val viewModel: DataAutoViewModel, private val index: Int) : Fragment() {
    private var _binding: FragmentEditDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = viewModel.getDataMyV()

        for (i in 0 until getData.size){
            if (getData[i].id == index){
                binding.editTextDrive.editText?.setText(getData[i].drive)
                binding.editTextColor.editText?.setText(getData[i].color)
                binding.editTextBody.editText?.setText(getData[i].body)
            }
        }

        binding.imageClose.setOnClickListener {
            bindingForList.floatingActionButton.visibility = View.VISIBLE
            DataAutoAdapter.opened = false
            parentFragmentManager.popBackStack()
        }

        binding.buttonAdd.setOnClickListener {
            val textDrive = binding.editTextDrive.editText?.text.toString()
            val textColor = binding.editTextColor.editText?.text.toString()
            val textBody = binding.editTextBody.editText?.text.toString()
            if (textDrive.isNotEmpty() && textColor.isNotEmpty() && textBody.isNotEmpty()) {
                viewModel.editAuto(textDrive, textColor, textBody, index)
                bindingForList.floatingActionButton.visibility = View.VISIBLE
                DataAutoAdapter.opened = false
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