package com.example.notapplication.view


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notapplication.repository.ManagerData
import com.example.notapplication.R
import com.example.notapplication.adapter.AdapterList
import com.example.notapplication.model.AbstractDataBase
import com.example.notapplication.databinding.ActivityListBinding
import com.example.notapplication.viewModel.MyViewModelFactory
import com.example.notapplication.viewModel.ViewModelForListActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ListNotActivity : AppCompatActivity() {
    private lateinit var preferec: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: ActivityListBinding
    private lateinit var viewModel: ViewModelForListActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })

        preferec = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        editor = preferec.edit()
        editor.apply()
        val daoNotesData = AbstractDataBase.buildDataBase(this).abstractFunDataBase()
        val managerData = ManagerData(daoNotesData)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR




        val factory = MyViewModelFactory(managerData)
        viewModel = ViewModelProvider(this, factory)[ViewModelForListActivity::class.java]

        val adapter = AdapterList(this, managerData.queryNotesData, viewModel)
        binding.recyclerViwe.layoutManager = LinearLayoutManager(this)
        binding.recyclerViwe.adapter = adapter


        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString()
                adapter.filter(query)
                true
            } else {
                false
            }
        }
        binding.searchInputLayout.setEndIconOnClickListener {
            // پاک کردن متن (خود TextInputLayout انجام میده ولی اینجا مطمئن می‌شیم)
            binding.searchEditText.text?.clear()

            // حذف فوکوس
            binding.searchEditText.clearFocus()

            // بستن کیبورد
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }




        if (managerData.queryNotesData.size > 0) binding.textView.visibility = View.INVISIBLE

        binding.floatingActionButton.setOnClickListener {
            it.setBackgroundColor(Color.parseColor("#EFD1C4"))
            val clickAnim = AnimationUtils.loadAnimation(this, R.anim.click_bounce)
            it.startAnimation(clickAnim)
            Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SettingTextActivity::class.java))
            }, 200)
        }



        binding.listFloatingActionButtonListActivity.setOnClickListener {
            it.setBackgroundColor(Color.parseColor("#EFD1C4"))

            val clickAnim = AnimationUtils.loadAnimation(this, R.anim.click_bounce)
            it.startAnimation(clickAnim)

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, AddNotActivity::class.java))
            }, 200)
        }


    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("خروج از برنامه")
            .setIcon(R.drawable.baseline_exit_to_app_24)
        .setMessage("شما میخواهید از برنامه خارج شوید؟")
        .setPositiveButton("بله") { dialog, _ ->
            finishAffinity()
        }
        .setNegativeButton("خیر") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
    }

}