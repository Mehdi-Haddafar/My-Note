package com.example.notapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notapplication.model.NoteBook
import com.example.notapplication.repository.ManagerData
import kotlinx.coroutines.launch

class ViewModelForNoteActivity(private val repository: ManagerData) : ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _des = MutableLiveData<String>()
    val des: LiveData<String> get() = _des

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

     private val _timeInMillis = MutableLiveData<Long>()
    val timeInMillis: LiveData<Long> get() = _timeInMillis

    private val _insertedNoteId = MutableLiveData<Long>()
    val insertedNoteId: LiveData<Long> get() = _insertedNoteId


    fun updateNote(
        position: Int,
        title: String,
        des: String,
        date: String,
        isEnabledTime: Boolean,
        timeInMillis: Long
    ) {
        val note = repository.getNoteById(position).id
        repository.update(
            id = note,
            titleForUpdate = title,
            desForUpdate = des,
            date = date,
            isEnabledTime = isEnabledTime,
            timeInMillis = timeInMillis
        )

    }

    fun insertNote(
        title: String,
        des: String,
        date: String,
        isEnabledTime: Boolean,
        timeInMillis: Long
    ) {
        val note = NoteBook(
            title = title,
            des = des,
            date = date,
            isEnabledTime = isEnabledTime,
            timeInMillis = timeInMillis
        )
        viewModelScope.launch {
            val insertedId = repository.insert(note)
            _insertedNoteId.postValue(insertedId)
        }

    }

    fun loadNoteDetails(position: Int) {
        val note = repository.getNoteById(position)
        _title.value = note.title
        _des.value = note.des
        _date.value = note.date
        _timeInMillis.value = note.timeInMillis
    }
}
