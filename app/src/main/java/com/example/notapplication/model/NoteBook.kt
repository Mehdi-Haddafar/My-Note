package com.example.notapplication.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "noteBook")
data class NoteBook(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "des")
    var des: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "timeInMillis")
    val timeInMillis: Long,
    @ColumnInfo(name = "isEnabledTime")
    val isEnabledTime: Boolean = true
)