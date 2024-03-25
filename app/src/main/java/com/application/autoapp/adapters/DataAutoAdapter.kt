package com.application.autoapp.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.autoapp.viewmodels.DataAutoViewModel
import com.application.autoapp.R
import com.application.autoapp.databinding.FragmentListBinding
import com.application.autoapp.frameworks.EditDataFragment
import com.application.autoapp.frameworks.ImageFullscreenFragment
import com.application.autoapp.room.DataAuto
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class DataAutoAdapter(private val binding: FragmentListBinding, private val viewModel: DataAutoViewModel, private val context:Context) :
    ListAdapter<DataAuto, DataAutoAdapter.DataAutoViewHolder>(DataAutoDiffCallback()) {
    companion object{
        var opened = false
        var act = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAutoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_two_my_app, parent, false)
        return DataAutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataAutoViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class DataAutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: DataAuto) {
            val tvName: TextView = itemView.findViewById(R.id.id_name)
            val tvDrive: TextView = itemView.findViewById(R.id.id_drive)
            val tvColor: TextView = itemView.findViewById(R.id.id_color)
            val tvBody: TextView = itemView.findViewById(R.id.id_body)
            val ivImage: ImageView = itemView.findViewById(R.id.id_image)
            val swipeButton: TextView = itemView.findViewById(R.id.textViewDelete)

            tvName.text = data.name
            val stringDrive = "Привод: ${data.drive}"
            val stringColor = "Цвет: ${data.color}"
            val stringBody = "Кузов: ${data.body}"
            tvDrive.text = stringDrive
            tvColor.text = stringColor
            tvBody.text = stringBody

            Picasso.get().load(data.image).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(ivImage)

            ivImage.setOnClickListener { view ->
                if (!opened){
                    showPopupMenu(view, data)
                    opened = true
                }
            }

            swipeButton.setOnClickListener {
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EditDataFragment(binding, viewModel, data.id))
                    .addToBackStack(null)
                    .commit()
                opened = true
                binding.floatingActionButton.visibility = View.GONE
            }
        }

        private fun showPopupMenu(view: View, data: DataAuto) {

            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.popup_menu)

            popupMenu.setOnDismissListener {
                if (!act){
                    opened = false
                }
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                act = true
                when (menuItem.itemId) {
                    R.id.menu_open -> {
                        val fragment = ImageFullscreenFragment(binding)
                        val bundle = Bundle()
                        bundle.putString("imageUrl", data.image)
                        fragment.arguments = bundle

                        (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()

                        binding.floatingActionButton.visibility = View.GONE
                        true
                    }
                    R.id.menu_download -> {
                        viewModel.loadPhotoFromGallery(context)
                        viewModel.actionImage = 0
                        viewModel.selectedCard = data.id
                        act = false
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            popupMenu.show()
        }
    }

    private class DataAutoDiffCallback : DiffUtil.ItemCallback<DataAuto>() {
        override fun areItemsTheSame(oldItem: DataAuto, newItem: DataAuto): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: DataAuto, newItem: DataAuto): Boolean {
            return oldItem == newItem
        }
    }
}