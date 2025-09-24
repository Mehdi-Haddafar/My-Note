package com.example.notapplication.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.notapplication.R
import com.example.notapplication.model.AbstractDataBase
import com.example.notapplication.repository.ManagerData
import com.example.notapplication.view.AddNotActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra("noteId", -1)
        val noteTitle = intent.getStringExtra("noteTitle")

        val clickIntent = Intent(context, AddNotActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("positionRecrcelerView", noteId)
            putExtra("boolin", true) // برای نمایش فقط، نه ویرایش
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId, // هر یادداشت آیدی منحصر به فرد داره
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        val channelId = "alarm_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ساخت نوتیفیکیشن‌چنل
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(null, attributes) // صدای نوتیفیکیشن رو خالی می‌ذاریم چون خودمون پخش می‌کنیم
            }
            notificationManager.createNotificationChannel(channel)
        }

        // ساخت نوتیفیکیشن
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("یادآوری یادداشت")
            .setContentText("زمان یادآوری یادداشت($noteTitle)فرا رسیده ")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(noteId, notification)

        // پخش صدای دلخواه (از raw/reminder_sound.mp3)
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.reminder_chime)
            mediaPlayer.start()

            // قطع صدا بعد از چند ثانیه (مثلاً ۵ ثانیه)
            Handler(Looper.getMainLooper()).postDelayed({
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.release()
            }, 5000)

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error playing custom sound: ${e.message}")
        }
    }
}
