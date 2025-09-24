package com.example.notapplication.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Interface {

    @Query("SELECT * FROM noteBook ORDER BY id DESC")
    fun queryNotesData(): MutableList<NoteBook>

    @Query("SELECT * FROM noteBook WHERE id = :id")
    fun getNoteById(id: Int): NoteBook

    @Insert
    suspend fun insertNote(notesData: NoteBook): Long

    @Delete
    fun deleteDataBase(notesData: NoteBook)

    @Query("UPDATE noteBook SET title=:titleForUpdate ,timeInMillis=:timeInMillis,isEnabledTime=:isEnabledTime, des =:desForUpdate , date=:date WHERE id =:id")
    fun updata(
        titleForUpdate: String,
        desForUpdate: String,
        id: Int,
        date: String,
        timeInMillis: Long,
        isEnabledTime: Boolean
    )
}