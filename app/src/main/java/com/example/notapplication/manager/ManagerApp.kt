package com.example.notapplication.manager

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

class ManagerApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { setLocaleToEnglish(it) })
    }

    override fun onCreate() {
        super.onCreate()

        // 1. غیرفعال کردن حالت شب (دارک مود)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // 2. شمارش تعداد دفعات اجرای اپ
        incrementAppLaunchCount()
    }

    private fun incrementAppLaunchCount() {
        val sharedPreferences = getSharedPreferences("LaunchPrefs", Context.MODE_PRIVATE)
        val currentCount = sharedPreferences.getInt("launch_count", 0)
        sharedPreferences.edit().putInt("launch_count", currentCount + 1).apply()
        Log.d("news", "App launched ${currentCount + 1} times")
    }

    // تابع تنظیم زبان و جهت چیدمان
    private fun setLocaleToEnglish(context: Context): Context {
        val locale = Locale("en")
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(Locale.ENGLISH) // صراحتاً LTR

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

}
