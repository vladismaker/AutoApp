package com.application.autoapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DataAutoDao {
    @Insert
    suspend fun insert(dataAuto: DataAuto)

    @Insert
    suspend fun insertAll(dataAutoList: List<DataAuto>)

    @Query("SELECT * FROM data_auto_table")
    suspend fun getAllDataAuto(): List<DataAuto>

    @Update
    suspend fun updateDataList(dataList: List<DataAuto>)
}