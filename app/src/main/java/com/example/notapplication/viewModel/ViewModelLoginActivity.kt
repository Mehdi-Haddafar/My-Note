package com.example.notapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ViewModelLoginActivity (application: Application) :  AndroidViewModel(application) {
    val userNameLoginActivity  = MutableLiveData<String>()
    val passwordLoginActivity = MutableLiveData<String>()

    val clickInTheSaveButton = MutableLiveData<Boolean>()

    init {
        clickInTheSaveButton.value = false

    }

  fun funSave () {
      val preferec = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
      val editor = preferec.edit()
      editor.apply()
        editor.putString("userNameget", userNameLoginActivity.value).apply()
        editor.putString("passwordget", passwordLoginActivity.value).apply()
        editor.putBoolean("openApp" , true).apply()

    }

    fun buttonSaveLoginActivity() {
        clickInTheSaveButton.value=true
    }


} 