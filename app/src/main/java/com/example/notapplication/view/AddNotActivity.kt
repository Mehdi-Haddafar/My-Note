package com.example.notapplication.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.notapplication.R
import com.example.notapplication.databinding.ActivityAddNotBinding
import com.example.notapplication.manager.AlarmReceiver
import com.example.notapplication.model.AbstractDataBase
import com.example.notapplication.repository.ManagerData
import com.example.notapplication.viewModel.ViewModelFactoryForViewModelNotActivity
import com.example.notapplication.viewModel.ViewModelForNoteActivity
import com.example.sequnsejenate.date.Date
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import java.util.*
import kotlin.math.log

class AddNotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNotBinding
    private lateinit var pref: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private val viewModel: ViewModelForNoteActivity by viewModels {
        ViewModelFactoryForViewModelNotActivity(
            ManagerData(
                AbstractDataBase.buildDataBase(this).abstractFunDataBase()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_not)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        edit = pref.edit()
        edit.remove("valueMillisAlarmTime").apply()

        val sizeText = pref.getFloat("sizeNoteText", 22F)
        binding.enterTitleEditTextAddNoteActivity.textSize = sizeText
        binding.enterDesEditTextAddNoteActivity.textSize = sizeText

        val textStyle = pref.getInt("styleNoteText", Typeface.NORMAL)
        val typefaceForTitle =
            Typeface.create(binding.enterTitleEditTextAddNoteActivity.typeface, textStyle)
        val typefaceForDes =
            Typeface.create(binding.enterDesEditTextAddNoteActivity.typeface, textStyle)

        binding.enterTitleEditTextAddNoteActivity.typeface = typefaceForTitle
        binding.enterDesEditTextAddNoteActivity.typeface = typefaceForDes

        val position = intent.getIntExtra("positionRecrcelerView", -1)


        if (intent.getBooleanExtra("boolin", false)) {
            showNoteDetails(position)
        } else if (intent.getBooleanExtra("boolinForUpdate", false)) {
            showNoteUpdateForm(position)
        } else {
            setupSaveButton()
        }
    }

    private fun showNoteDetails(position: Int) {
        binding.apply {
            addNotTextViewAddNotActivity.text = "نمایش یاداشت"
            enterTitleEditTextAddNoteActivity.isFocusable = false
            enterDesEditTextAddNoteActivity.isFocusable = false
            saveButton.textSize = 20F
            saveButton.text = "برگشت به صفحه قبلی"
        }

        viewModel.loadNoteDetails(position)
        viewModel.title.observe(this) {
            binding.enterTitleEditTextAddNoteActivity.setText(it)
        }

        viewModel.des.observe(this) {
            binding.enterDesEditTextAddNoteActivity.setText(it)
        }

        viewModel.timeInMillis.observe(this) {
            binding.btnSetAlarm.textSize = 13F
            if (it == 0L) binding.btnSetAlarm.setText("زمان یادآوری ثبت نشده است")
            else binding.btnSetAlarm.text = formatPersianDateTime(it)
        }

        viewModel.date.observe(this) {
            if (it == "undefined") {
                binding.forDateTextAddNoteActivity.text = "!تاریخی ثبت نشده است"
            } else binding.forDateTextAddNoteActivity.text = it
        }

        binding.saveButton.setOnClickListener {
            startActivity(Intent(this, ListNotActivity::class.java))
        }
    }

    private fun showNoteUpdateForm(position: Int) {
        var noteTitle = ""
        binding.apply {
            addNotTextViewAddNotActivity.text = "ویرایش یاداشت"
            enterTitleEditTextAddNoteActivity.isFocusableInTouchMode = true
            enterDesEditTextAddNoteActivity.isFocusableInTouchMode = true
            viewModel.date.observe(this@AddNotActivity) {
                binding.forDateTextAddNoteActivity.text = it
            }
            saveButton.text = getString(R.string.save)
        }

        viewModel.loadNoteDetails(position)
        viewModel.title.observe(this) {
            noteTitle = it
            binding.enterTitleEditTextAddNoteActivity.setText(it)

        }
        viewModel.des.observe(this) {
            binding.enterDesEditTextAddNoteActivity.setText(it)
        }

        viewModel.timeInMillis.observe(this) {
            binding.btnSetAlarm.textSize = 13F
            if (it == 0L) binding.btnSetAlarm.setText("زمان یادآوری ثبت نشده است")
            else binding.btnSetAlarm.text = formatPersianDateTime(it)
        }

        binding.btnSetAlarm.setOnClickListener {
            if (hasAllPermissions()) {
                showPersianDatePicker { year, month, day ->
                    showTimePicker { hour, minute ->
                        onTimePicked(year, month, day, hour, minute)
                    }
                }
            } else requestAllPermissionsIfNeeded()
        }



        binding.saveButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this@AddNotActivity).apply {
                setMessage("یاداشت ویرایش  شود؟")
                setPositiveButton("بله") { _, _ ->
                    val title = binding.enterTitleEditTextAddNoteActivity.text.toString()
                    val des = binding.enterDesEditTextAddNoteActivity.text.toString()
                    val persianDate = Date().getPersianDate()
                    val date = persianDate

                    if (title.isEmpty() || des.isEmpty()) {
                        Toast.makeText(
                            this@AddNotActivity, "نمیتوانید خالی بگذارید", Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    } else {
                        val valueMillisAlarmTime = pref.getLong("valueMillisAlarmTime", 0L)
                        val isEnableTime = valueMillisAlarmTime != 0L
                        viewModel.updateNote(
                            position,
                            title,
                            des,
                            date,
                            timeInMillis = valueMillisAlarmTime,
                            isEnabledTime = isEnableTime
                        )
                        if (isEnableTime) {
                            Log.d("testPosition1", "showNoteUpdateForm:$position ")

                            cancelAlarm(this@AddNotActivity, position)
                            setAlarm(
                                noteId = position,
                                timeInMillis = valueMillisAlarmTime,
                                noteTitle = title
                            )

                        }
                        edit.remove("valueMillisAlarmTime")
                        startActivity(Intent(this@AddNotActivity, ListNotActivity::class.java))
                        Toast.makeText(
                            this@AddNotActivity, "یاداشت و تاریخ آن ویرایش شدند", Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
                setNegativeButton("خیر") { dialog, _ -> dialog.dismiss() } // ���� �?��
                show()

            }
        }
    }

    // When the user wants to add a note
    private fun setupSaveButton() {

        binding.btnSetAlarm.setOnClickListener {
            if (hasAllPermissions()) {
                showPersianDatePicker { year, month, day ->
                    showTimePicker { hour, minute ->
                        onTimePicked(year, month, day, hour, minute)
                    }
                }
            } else requestAllPermissionsIfNeeded()
        }

        val persianDate = Date().getPersianDate()
        binding.forDateTextAddNoteActivity.text = persianDate


        binding.saveButton.setOnClickListener {
            val title = binding.enterTitleEditTextAddNoteActivity.text.toString()
            val des = binding.enterDesEditTextAddNoteActivity.text.toString()
            val date = binding.forDateTextAddNoteActivity.text.toString()
            if (title.isEmpty() || des.isEmpty()) Toast.makeText(
                this,
                "نمیتوانید خالی بگذارید",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val valueMillisAlarmTime = pref.getLong("valueMillisAlarmTime", 0L)
                val isEnableTime = valueMillisAlarmTime != 0L
                viewModel.insertNote(
                    title,
                    des,
                    date,
                    timeInMillis = valueMillisAlarmTime,
                    isEnabledTime = isEnableTime
                )
                viewModel.insertedNoteId.observe(this) {
                    if (isEnableTime) {
                        setAlarm(
                            noteId = it.toInt(),
                            timeInMillis = valueMillisAlarmTime,
                            noteTitle = title
                        )
                        Log.d("testPosition", it.toInt().toString())
                    }
                }
                edit.remove("valueMillisAlarmTime")
                startActivity(Intent(this, ListNotActivity::class.java))
                finish()
            }
        }
    }

    private fun showTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute -> onTimeSelected(selectedHour, selectedMinute) },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun onTimePicked(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val persianCalendar = PersianCalendar()

        persianCalendar.setPersianDate(year, month, day)
        persianCalendar.set(Calendar.HOUR_OF_DAY, hour)
        persianCalendar.set(Calendar.MINUTE, minute)
        persianCalendar.set(Calendar.SECOND, 0)
        persianCalendar.set(Calendar.MILLISECOND, 0)

        persianCalendar.timeZone = TimeZone.getTimeZone("Asia/Tehran")

        val alarmTimeInMillis = persianCalendar.timeInMillis
        val now = System.currentTimeMillis()

        if (alarmTimeInMillis <= now) {
            Toast.makeText(
                this, "زمان انتخاب شده گذشته است. لطفا زمان آینده انتخاب کنید.", Toast.LENGTH_LONG
            ).show()
            return
        } else {
            binding.btnSetAlarm.text = "$year/${month + 1}/$day - $hour:$minute"
            Toast.makeText(
                this,
                "زمان یادآوری تنظیم شد: $year/${month + 1}/$day - $hour:$minute",
                Toast.LENGTH_LONG
            ).show()
            edit.putLong("valueMillisAlarmTime", alarmTimeInMillis).apply()
        }
    }


    private fun showPersianDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
        val persianCalendar = PersianCalendar()
        val datePicker = PersianDatePickerDialog(this).setInitDate(persianCalendar)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(persianPickerDate: PersianPickerDate?) {
                    if (persianPickerDate != null) {
                        onDateSelected(
                            persianPickerDate.persianYear,
                            persianPickerDate.persianMonth,
                            persianPickerDate.persianDay
                        )
                    }
                }

                override fun onDismissed() {}
            })
        datePicker.show()
    }

    private fun setAlarm(noteId: Int, timeInMillis: Long, noteTitle: String) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("noteId", noteId)
            putExtra("noteTitle", noteTitle)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, noteId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
        )

    }

    private fun requestAllPermissionsIfNeeded(): Boolean {
        explainNotificationPermissionIfNeeded()
        explainExactAlarmPermissionIfNeeded()
        return hasAllPermissions()
    }


    private fun hasAllPermissions(): Boolean {
        return hasNotificationPermission() && hasExactAlarmPermission()
    }


    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun explainNotificationPermissionIfNeeded() {
        if (!hasNotificationPermission()) {
            MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog).setTitle("مجوز اعلان")
                .setMessage("برای نمایش اعلان جهت یادآوری یادداشت مدنظر شما، به اجازه این دسترسی نیاز داریم.")
                .setPositiveButton("اجازه بده") { dialog, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001
                    )
                    dialog.dismiss()
                }.setNegativeButton("فعلاً نه") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    private fun explainExactAlarmPermissionIfNeeded() {
        if (!hasExactAlarmPermission()) {
            MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog).setTitle("اجازه تنظیم آلارم دقیق")
                .setMessage("برای اینکه یادآوری ها به‌صورت دقیق اجرا بشن، به مجوز دقیق الارم نیاز داریم")
                .setPositiveButton("فعال‌سازی") { dialog, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                    dialog.dismiss()
                }.setNegativeButton("فعلاً نه") { dialog, _ -> dialog.dismiss() }.show()
        }
    }

    fun formatPersianDateTime(timeInMillis: Long): String {
        val calendar = PersianCalendar()
        calendar.timeInMillis = timeInMillis

        val month = calendar.persianMonth
        val day = calendar.persianDay
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return String.format("یادآوری در: %02d:%02d-%02d/%02d", hour, minute, month, day)
    }

    private fun cancelAlarm(context: Context, noteId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, noteId, intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel() // اضافه کردن این خط برای اطمینان
    }
}
