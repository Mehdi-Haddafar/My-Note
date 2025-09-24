package com.example.notapplication.repository

import com.example.notapplication.model.Interface
import com.example.notapplication.model.NoteBook

class ManagerData(val interfaceData: Interface) {

    val queryNotesData = interfaceData.queryNotesData()
    fun getNoteById(id: Int): NoteBook = interfaceData.getNoteById(id)

    suspend fun insert(notesData: NoteBook): Long = interfaceData.insertNote(notesData)

    fun delete(notesData: NoteBook) = interfaceData.deleteDataBase(notesData)

    fun update(
        titleForUpdate: String,
        desForUpdate: String,
        date: String,
        id: Int,
        timeInMillis: Long,
        isEnabledTime: Boolean
    ) = interfaceData.updata(titleForUpdate, desForUpdate, id, date, timeInMillis, isEnabledTime)
}