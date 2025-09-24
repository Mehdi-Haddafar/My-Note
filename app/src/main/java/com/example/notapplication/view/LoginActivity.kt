package com.example.notapplication.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notapplication.R
import com.example.notapplication.databinding.LoginActivityBinding
import com.example.notapplication.viewModel.ViewModelLoginActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var preferec: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)

        val viewModelLoginActivity = ViewModelProvider(this)[ViewModelLoginActivity::class.java]
        binding.dataForEditTextLoginActivity = viewModelLoginActivity

        preferec = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        editor = preferec.edit()
        editor.apply()


        val openApp: Boolean = preferec.getBoolean("openApp", false)
        if (!openApp) {
            binding.enterButtonLoginActivity.text = " ذخیره"
            binding.cardViewPassConfirmaitonEditTextLoginActivity.visibility = View.VISIBLE
        }
        else {
            binding.cardViewPassConfirmaitonEditTextLoginActivity.visibility = View.GONE
            binding.guidelineTop.setGuidelinePercent(0.32f)
            binding.guidelineTopAndBottomEditeText.setGuidelinePercent(0.52f)
            binding.guidelineBottom.setGuidelinePercent(0.62f)
            binding.enterButtonLoginActivity.text = "ورود"
        }
        viewModelLoginActivity.clickInTheSaveButton.observe(this) {
            if (it == true) {
                val userName = binding.userNameEditTextLoginActivity.text.toString()
                val password = binding.passvordEditTextLoginActivity.text.toString()

                Log.d("testUserName" , userName+password)
                val intent = Intent(this, ListNotActivity::class.java)

                if (openApp) {
                    if (userName == preferec.getString("userNameget", "") &&
                        password == preferec.getString("passwordget", "")
                    ) {
                        startActivity(intent)
                        finish()}

                    else Toast.makeText(this, getString(R.string.showMassageToastMaghadirYekiNemibashad), Toast.LENGTH_SHORT).show()
                } else {

                    if (userName == "" || password == "") {
                        Toast.makeText(
                            this,
                            getString(R.string.showMassageToastSpase),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (userName.startsWith(" ") || password.startsWith(" "))
                        Toast.makeText(
                            this,
                            getString(R.string.showMassageToastStartWith),
                            Toast.LENGTH_SHORT
                        ).show()
                    else if (userName.length < 4) Toast.makeText(
                        this, "نام کاربری باید بیشتر از 4 کارکتر باشد", Toast.LENGTH_SHORT
                    ).show()
                    else if (password.length < 4) Toast.makeText(
                        this, "پسورد باید بیشتر از 4 کاراکتر باشد",
                        Toast.LENGTH_SHORT
                    ).show()
                    else if(password != binding.PassConfirmaitonEditTextLoginActivity.text.toString()){
                        Toast.makeText(this, "پسورد هایی که وارد کرده اید فرق میکنند ", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        viewModelLoginActivity.userNameLoginActivity.value = userName
                        viewModelLoginActivity.passwordLoginActivity.value = password
                        viewModelLoginActivity.funSave()
                        startActivity(Intent(this,SettingTextActivity::class.java))
                        finish()
                    }

                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userName", binding.userNameEditTextLoginActivity.text.toString())
        outState.putString("password", binding.passvordEditTextLoginActivity.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.userNameEditTextLoginActivity.setText(savedInstanceState.getString("userName"))
        binding.passvordEditTextLoginActivity.setText(savedInstanceState.getString("password"))
    }

}
