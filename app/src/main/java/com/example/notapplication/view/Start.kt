package com.example.notapplication.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notapplication.databinding.ActivityStartBinding

class Start : AppCompatActivity() {


    private lateinit var binding: ActivityStartBinding
    private lateinit var preferec: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // نمایش لودینگ
        binding.loadingProgress.visibility = View.VISIBLE

        // گرفتن SharedPreferences
        preferec = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        editor = preferec.edit()
        editor.apply()

        // تاخیر ۵ ثانیه‌ای و رفتن به Activity بعدی
        Handler(Looper.getMainLooper()).postDelayed({

            val nextActivity = if (preferec.getBoolean("openApp", false)) {
                LoginActivity::class.java
            } else {
                ListNotActivity::class.java
            }

            startActivity(Intent(this, nextActivity))
            finish()

        }, 2000) // ۵۰۰۰ میلی‌ثانیه = ۵ ثانیه

    }
}
