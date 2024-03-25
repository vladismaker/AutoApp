package com.application.autoapp.viewmodels

import android.content.Context
import android.net.Uri
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.autoapp.App
import com.application.autoapp.R
import com.application.autoapp.repositories.DataAutoRepository
import com.application.autoapp.room.DataAuto
import com.application.autoapp.room.DatabaseBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.UUID

class DataAutoViewModel(private val repository: DataAutoRepository) : ViewModel() {
    private val _dataList = MutableLiveData<MutableList<DataAuto>>()
    val dataList: LiveData<MutableList<DataAuto>> = _dataList
    private val checkedItems = booleanArrayOf(true, true, true)
    var sorted = true
    var selectedCard = 0
    var actionImage = 0
    private var myAddImage = "https://cdn.onlinewebfonts.com/svg/download_103323.png"

    constructor() : this(DataAutoRepository(DatabaseBuilder.getInstance(App.context).dataAutoDao()))

    val selectedImageLiveData = MutableLiveData<Uri>()

    suspend fun loadData() {
        _dataList.value = repository.getAllData()
    }

    fun setDialogs(context: Context){
        val options = arrayOf("Передний", "Задний", "Полный")

        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle("Привод:")
        builder.setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }

        builder.background = ResourcesCompat.getDrawable(context.resources, R.drawable.background_for_filter, null)

        builder.setPositiveButton("Применить") { dialog, _ ->
            val selectedOptions = mutableListOf<String>()
            for (i in options.indices) {
                if (checkedItems[i]) {
                    selectedOptions.add(options[i])
                }
            }
            applyFilter(selectedOptions)
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun applyFilter(selectedOptions: List<String>) {
        val filteredList = mutableListOf<DataAuto>()

        App.ioScope.launch {
            val s = repository.getAllData()
            for (item in s) {
                if (selectedOptions.contains(item.drive)) {
                    filteredList.add(item)
                }
            }
        }
        updateDataList(filteredList)
    }

    fun startSorting(){
        val sortedList = _dataList.value?.toMutableList() ?: mutableListOf()
        if (sorted) {
            sortedList.sortBy { it.name }
        } else {
            sortedList.sortByDescending { it.name }
        }
        _dataList.value = sortedList
    }

    private fun updateDataList(data: MutableList<DataAuto>) {
        _dataList.value = data
    }

    fun loadPhotoFromGallery(context: Context) {
        repository.loadPhotoFromGallery(context)
    }

    fun getDataMyV():MutableList<DataAuto>{
        return _dataList.value!!
    }

    fun handleSelectedImage(selectedImageUri:Uri?){
        if (actionImage == 0 ){
            val currentDataList = _dataList.value ?: return
            val updatedDataList = currentDataList.map { item ->
                if (item.id == selectedCard) {
                    item.copy(image = selectedImageUri.toString())
                } else {
                    item
                }
            }.toMutableList()

            updateDataList(updatedDataList)

            App.ioScope.launch {
                repository.updateDataList(updatedDataList)
            }
        }else if(actionImage == 1){
            myAddImage = selectedImageUri.toString()
        }

    }

    fun getImageUri():String{
        return myAddImage
    }

    fun addAuto(strName:String, strImage:String, strDrive:String, strColor:String, strBody:String){
        val newDataItem = DataAuto(UUID.randomUUID().hashCode(), strName, strImage, strDrive, strColor, strBody)

        val currentDataList = _dataList.value?.toMutableList() ?: mutableListOf()
        currentDataList.add(newDataItem)
        _dataList.value = currentDataList
        val db = DatabaseBuilder.getInstance(App.context).dataAutoDao()
        App.ioScope.launch{
            db.insert(newDataItem)
        }
    }

    fun editAuto(strDrive:String, strColor:String, strBody:String, index:Int){
        val currentDataList = _dataList.value ?: return
        val updatedDataList = currentDataList.map { item ->
            if (item.id == index) {
                item.copy(drive = strDrive, color = strColor, body = strBody)
            } else {
                item
            }
        }.toMutableList()

        updateDataList(updatedDataList)
        App.ioScope.launch {
            repository.updateDataList(updatedDataList)
        }
    }
}