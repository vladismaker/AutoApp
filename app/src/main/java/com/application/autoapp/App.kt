package com.application.autoapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.application.autoapp.room.AppDatabase
import com.application.autoapp.room.DataAuto
import com.application.autoapp.room.DatabaseBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        val db = DatabaseBuilder.getInstance(context)
        ioScope.launch {
            if (db.dataAutoDao().getAllDataAuto().isEmpty()){
                addInitialData(db)
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()

        ioScope.cancel()
    }

    private suspend fun addInitialData(db: AppDatabase) {
        val initialData = listOf(
            DataAuto(UUID.randomUUID().hashCode(),"Audi", "https://images.squarespace-cdn.com/content/v1/5849e53f46c3c4a1e3e0ae19/1517544841300-KRGVCUA02C70XLPMGM0L/ke17ZwdGBToddI8pDm48kFAv0TH53TL_Tc6nk2yyiLJ7gQa3H78H3Y0txjaiv_0fDoOvxcdMmMKkDsyUqMSsMWxHk725yiiHCCLfrh8O1z5QPOohDIaIeljMHgDF5CVlOqpeNLcJ80NK65_fV7S1UQhVzR9W-rZXUArTgeatnZ8ZUAsjFYFy43EmZOaL9QL9XejQve6EWhtsEXNp5cp4uw/New-Web-6.jpg", "Полный", "Серый", "Лифтбек"),
            DataAuto(UUID.randomUUID().hashCode(),"BMW", "https://i.pinimg.com/736x/8f/f1/5f/8ff15f8695f90378829dd8706248fb92.jpg", "Задний", "Белый", "Седан"),
            DataAuto(UUID.randomUUID().hashCode(),"Porsche", "https://www.acquiremag.com/.image/t_share/MTI2MTQxMTQxODU4MDMwODY2/911cab.jpg", "Полный", "Красный", "Кабриолет")
        )

        db.dataAutoDao().insertAll(initialData)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var ioScope = CoroutineScope(Dispatchers.IO)
    }
}