package com.application.autoapp.repositories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.application.autoapp.App
import com.application.autoapp.room.DataAuto
import com.application.autoapp.room.DataAutoDao
import com.application.autoapp.room.DatabaseBuilder

class DataAutoRepository(private val dataAutoDao: DataAutoDao) {

    fun loadPhotoFromGallery(context: Context) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(context.packageManager) != null) {
            (context as? Activity)?.startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }
    }

    suspend fun updateDataList(dataList: List<DataAuto>) {
        dataAutoDao.updateDataList(dataList)
    }

    suspend fun getAllData():MutableList<DataAuto>{
        val mu: MutableList<DataAuto> = mutableListOf()
        val db = DatabaseBuilder.getInstance(App.context)

        val dataAutoDao = db.dataAutoDao()

        val dataList = dataAutoDao.getAllDataAuto()

        for (data in dataList) {
            mu.add(data)
        }
        return mu
    }

    companion object {
        const val REQUEST_PICK_IMAGE = 1001
    }
}