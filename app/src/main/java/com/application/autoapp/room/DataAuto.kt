package com.application.autoapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_auto_table")
data class DataAuto(
    @PrimaryKey val id:Int,
    var name: String,
    var image: String,
    var drive: String,
    var color: String,
    var body: String
)
