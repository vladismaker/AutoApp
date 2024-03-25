package com.application.autoapp.frameworks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.autoapp.App
import com.application.autoapp.R
import com.application.autoapp.adapters.DataAutoAdapter
import com.application.autoapp.databinding.FragmentListBinding
import com.application.autoapp.repositories.DataAutoRepository
import com.application.autoapp.viewmodels.DataAutoViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DataAutoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!checkInternet()){
            Toast.makeText(requireContext(), "Проверьте интернет-соединение!", Toast.LENGTH_SHORT).show()
        }

        viewModel = ViewModelProvider(this)[DataAutoViewModel::class.java]

        val adapter = DataAutoAdapter(binding, viewModel, requireActivity())
        binding.recyclerViewMy.adapter = adapter
        binding.recyclerViewMy.layoutManager = LinearLayoutManager(context)

        viewModel.dataList.observe(viewLifecycleOwner, Observer { data ->
            adapter.submitList(data)
        })

        lifecycleScope.launch {
            viewModel.loadData()
        }

        setItemTouchHelper()

        binding.floatingActionButton.setOnClickListener {
            binding.floatingActionButton.visibility = View.GONE
            DataAutoAdapter.opened = true
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddAutoFragment(binding, viewModel))
                .addToBackStack(null)
                .commit()
        }

        binding.sorting.setOnClickListener {
            if (viewModel.sorted){
                viewModel.sorted = false
                binding.sorting.rotation = 180f

            }else{
                viewModel.sorted = true
                binding.sorting.rotation = 0f
            }
            val color = ContextCompat.getColor(App.context, R.color.transparent)
            binding.sorting.setColorFilter(color)
            viewModel.startSorting()
        }

        binding.filter.setOnClickListener {
            viewModel.setDialogs(requireActivity())
        }
    }

    private fun setItemTouchHelper(){
        ItemTouchHelper(object :ItemTouchHelper.Callback(){
            private val limitScrollX = dipToPx(100f, requireActivity())
            private var currentScrollX = 0
            private var currentScrollXWhenInActive = 0
            private var initXWhenInActive = 0f
            private var firstInActive = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    if (dX == 0f){
                        currentScrollX = viewHolder.itemView.scrollX
                        firstInActive = true
                    }

                    if (isCurrentlyActive){
                        var scrollOffset = currentScrollX + (-dX).toInt()
                        if (scrollOffset>limitScrollX){
                            scrollOffset = limitScrollX
                        }else if(scrollOffset<0){
                            scrollOffset = 0
                        }

                        viewHolder.itemView.scrollTo(scrollOffset, 0)
                    }else{
                        if (firstInActive){
                            firstInActive = false
                            currentScrollXWhenInActive = viewHolder.itemView.scrollX
                            initXWhenInActive = dX
                        }

                        if (viewHolder.itemView.scrollX < limitScrollX){
                            viewHolder.itemView.scrollTo((currentScrollXWhenInActive*dX/initXWhenInActive).toInt(), 0)
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (viewHolder.itemView.scrollX>limitScrollX){
                    viewHolder.itemView.scrollTo(limitScrollX, 0)
                }else if(viewHolder.itemView.scrollX<0){
                    viewHolder.itemView.scrollTo(0, 0)
                }
            }
        }).apply {
            attachToRecyclerView(binding.recyclerViewMy)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DataAutoRepository.REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val copiedImageUri = selectedImageUri?.let { copyImageToInternalStorage(it) }

            viewModel.handleSelectedImage(copiedImageUri)
            selectedImageUri?.let {
                viewModel.selectedImageLiveData.value = it
            }
        }
    }

    private fun copyImageToInternalStorage(selectedImageUri: Uri): Uri? {
        val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri)
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val outputStream = FileOutputStream(File(requireContext().filesDir, fileName))
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return Uri.fromFile(File(requireContext().filesDir, fileName))
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun dipToPx(dipValue: Float, context:Context):Int{
        return (dipValue * context.resources.displayMetrics.density).toInt()
    }

    private fun checkInternet(): Boolean {
        val connectivityManager = App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNet = connectivityManager.activeNetwork
        if (activeNet != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNet)
            return networkCapabilities != null && networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        return false
    }
}