package com.example.notapplication.viewModel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.example.notapplication.R
import com.example.notapplication.model.NoteBook
import com.example.notapplication.repository.ManagerData
import com.example.notapplication.view.AddNotActivity
import com.example.notapplication.view.ListNotActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ViewModelForListActivity(val repository: ManagerData) : ViewModel() {

    val id: Int = 0

    fun getNoteItem(id : Int ): NoteBook{
        return (repository.getNoteById(id))
    }

    fun onAddNoteClicked(context: Context, position: Int) {
        val intent = Intent(context, AddNotActivity::class.java).apply {
            putExtra("positionRecrcelerView", position)
            putExtra("boolin", true)
        }
        context.startActivity(intent)
    }

    fun onUpdateNoteClicked(context: Context, position: Int) {
        val intent = Intent(context, AddNotActivity::class.java).apply {
            putExtra("positionRecrcelerView", position)
            putExtra("boolinForUpdate", true)
        }
        context.startActivity(intent)
    }

    fun deleteNoteActivity( notLoist: MutableList<NoteBook> , position: Int , context: Context){
        Log.d("forDelete" , repository.getNoteById(position).toString())
        val builder = MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
        builder.setTitle("حذف یادداشت")
            .setMessage("آیا مطمئن هستید که می‌خواهید این یادداشت حذف شود؟")
            .setPositiveButton("بله") { dialog, _ ->
                Log.d("forDelete", repository.getNoteById(position).toString())
                repository.delete(repository.getNoteById(position))
                notLoist.remove(repository.getNoteById(position))
                Toast.makeText(context, "یادداشت حذف شد!", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, ListNotActivity::class.java))
            }
            .setNegativeButton("خیر") { dialog, _ ->
                dialog.dismiss()
            }
            .show()


        val dialog : AlertDialog = builder.create()
            dialog.show()

    }
    // تابع اصلی برای نمایش نظر
    fun requestFeedback(context: Context) {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val hasDeclinedForever = prefs.getBoolean("has_declined_forever", false)

        // اگر کاربر قبلاً گزینه "هرگز" را انتخاب کرده، دیالوگ نشان داده نشود
        if (hasDeclinedForever) {
            return
        }

        // دیالوگ اول: پرسش اولیه
        val builder = AlertDialog.Builder(context)
        builder.setTitle("همایت شما از ما")
        builder.setMessage("آیا مایلید به ما نظر دهید؟")

        builder.setPositiveButton("بله") { dialog, _ ->
            // نمایش دیالوگ بازخورد اصلی
            showFeedbackDialog(context)
            dialog.dismiss()
        }

        builder.setNegativeButton("هرگز") { dialog, _ ->
            // ذخیره اینکه کاربر دیگر نمی‌خواهد دیالوگ را ببیند
            prefs.edit().putBoolean("has_declined_forever", true).apply()
            Toast.makeText(context, "نظر دادن غیرفعال شد.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNeutralButton("بعداً") { dialog, _ ->
            // بدون ذخیره چیزی، دیالوگ در دفعات بعدی باز می‌شود
            dialog.dismiss()
        }

        builder.create().show()
    }

    // تابع دیالوگ بازخورد اصلی
    fun showFeedbackDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("نظر شما درباره این اپلیکیشن؟")
        builder.setMessage("لطفاً نظر خود را با ما به اشتراک بگذارید.")

        builder.setPositiveButton("عالیه") { dialog, _ ->
            // هدایت به مارکت
            openAppInMarket(context)
            dialog.dismiss()
        }

        builder.setNegativeButton("نیاز به بهبود داره") { dialog, _ ->
            // ذخیره یا ارسال نظر منفی
            saveNegativeFeedback(context, "کاربر نظر منفی داد.")
            dialog.dismiss()
        }

        builder.create().show()
    }

    // تابع هدایت به کافه‌بازار
    fun openAppInMarket(context: Context) {
        val appPackageName = "com.example.notapplication" // پکیج نام اپلیکیشن
        val marketUrl = "bazaar://details?id=$appPackageName&ref=share"
        val webUrl = "http://cafebazaar.ir/app/?id=$appPackageName&ref=share"

        try {
            // Intent برای باز کردن کافه‌بازار
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(marketUrl)
                setPackage("com.farsitel.bazaar") // مشخص کردن کافه‌بازار به عنوان مقصد
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // اگر کافه‌بازار نصب نبود، لینک وب را باز می‌کند
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
            context.startActivity(webIntent)
        }
    }

    // تابع ذخیره بازخورد منفی
    fun saveNegativeFeedback(context: Context, feedback: String) {
        val fileName = "negative_feedback.txt" // نام فایل ذخیره
        try {
            context.openFileOutput(fileName, Context.MODE_APPEND).use { output ->
                output.write("$feedback\n".toByteArray())
            }
            Toast.makeText(context, "بازخورد شما ذخیره شد. متشکریم!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "خطایی در ذخیره بازخورد رخ داد!", Toast.LENGTH_SHORT).show()
        }
    }

    fun resetLaunchCount(context: Context) {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        prefs.edit().putInt("launch_count", 0).apply()
        Toast.makeText(context, "شمارش باز شدن برنامه بازنشانی شد.", Toast.LENGTH_SHORT).show()
    }

}
