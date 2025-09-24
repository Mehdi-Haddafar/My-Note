package com.example.notapplication.view

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.notapplication.R
import com.example.notapplication.databinding.ActivitySettingTextBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

class SettingTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingTextBinding
    private lateinit var preferec: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivitySettingTextBinding.inflate(layoutInflater)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        supportActionBar?.hide()

        preferec = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        editor = preferec.edit()
        editor.apply()
        setContentView(binding.root)
        var styleText = preferec.getInt("styleNoteText", 0)
        when (styleText) {
            Typeface.BOLD -> binding.tabLayout.getTabAt(0)?.select()
            Typeface.NORMAL -> binding.tabLayout.getTabAt(1)?.select()
            Typeface.ITALIC -> binding.tabLayout.getTabAt(2)?.select()
        }

        var sizeText = preferec.getFloat("sizeNoteText", 22F)
        binding.textViewForSampleText.textSize = sizeText
        binding.seekBarTextSize.progress = sizeText.toInt()
        binding.btnLockActivity.setOnClickListener {
            startActivity(
                Intent(
                    this, LoginActivity::class.java
                )
            )
        }

        binding.imageViewForQuestionButton.setOnClickListener {
            MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog).setTitle("قفل کردن برنامه")
                .setMessage("اگر می‌خواهید از یادداشت‌های خود محافظت کنید و نام کاربری و رمز عبوری برای آن طراحی کنید، با زدن این دکمه می‌توانید این کار را انجام دهید!")
                .setPositiveButton("متوجه شدم") { dialog, _ ->
                    dialog.dismiss()
                }.show()

        }

        binding.imageViewForTextViewForSampleText.setOnClickListener {
            MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog).setTitle("متن نمونه")
                .setMessage("شما می‌توانید با تغییر اندازه و نوع متن‌های یادداشت‌ها، که در پایین قرار دارد، نمونه متنی که ایجاد می‌شود را ببینید!")
                .setPositiveButton("متوجه شدم") { dialog, _ ->
                    dialog.dismiss()
                }.show()

        }

        binding.seekBarTextSize.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                sizeText = progress.toFloat()
                binding.textViewForSampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeText)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.textViewForSampleText.setTypeface(null, Typeface.BOLD)
                        styleText = Typeface.BOLD
                    }

                    1 -> {
                        binding.textViewForSampleText.setTypeface(null, Typeface.NORMAL)
                        styleText = Typeface.NORMAL
                    }

                    2 -> {
                        binding.textViewForSampleText.setTypeface(null, Typeface.ITALIC)
                        styleText = Typeface.ITALIC
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.saveButton.setOnClickListener {
            editor.putFloat("sizeNoteText", sizeText)
            editor.putInt("styleNoteText", styleText)
            startActivity(Intent(this, ListNotActivity::class.java))
            editor.apply()
        }
    }
}