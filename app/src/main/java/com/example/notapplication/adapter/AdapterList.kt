package com.example.notapplication.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notapplication.databinding.RecyclerviweBinding
import com.example.notapplication.manager.AlarmReceiver
import com.example.notapplication.model.NoteBook
import com.example.notapplication.viewModel.ViewModelForListActivity


class AdapterList(
    private var context: Context,
    private var notLoist: MutableList<NoteBook>,
    private val viewModelForListActivity: ViewModelForListActivity
) :
    RecyclerView.Adapter<AdapterList.myViweHolder>() {
    private var idList: MutableList<Int> = notLoist.map { it.id }.toMutableList()


    inner class myViweHolder(var binding: RecyclerviweBinding) :
        RecyclerView.ViewHolder(binding.root) {
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViweHolder =
        myViweHolder(
            RecyclerviweBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun getItemCount(): Int = notLoist.size


    override fun onBindViewHolder(holder: myViweHolder, position: Int) {
        val id = idList[position]
        val noteItem = viewModelForListActivity.getNoteItem(id)

        Log.d("testList" , position.toString())

        holder.binding.titleTextViewRecyclerViweActvity.text = noteItem.title
        holder.binding.desTextViweRecyclerViwe.text = noteItem.des
        holder.binding.textViewForDateInTheRecyclerViewLayout.text = noteItem.date




        fun setClickListener(vararg views: View) {
            for (view in views) {
                view.setOnClickListener {
                    viewModelForListActivity.onAddNoteClicked( context , noteItem.id)
                }
            }
        }

        setClickListener(
            holder.binding.recyclerViweNotes,
            holder.binding.showNoteLayout,
            holder.binding.desTextViweRecyclerViwe,
            holder.binding.titleTextViewRecyclerViweActvity
        )


        holder.binding.updateNoteLayout.setOnClickListener {
            viewModelForListActivity.onUpdateNoteClicked(context , noteItem.id)
        }

        holder.binding.layoutDeleteNote.setOnClickListener {
            Log.d("testForCancel" , noteItem.toString())
            if (noteItem.isEnabledTime){
                cancelAlarm(context , noteItem.id)
            }
            viewModelForListActivity.deleteNoteActivity( notLoist , noteItem.id  , context)
        }



    }
    val originalListCustomers = notLoist.toMutableList() // ذخیره لیست اصلی


    fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            originalListCustomers
        } else {
            originalListCustomers.filter { item ->
                item.title.contains(query, ignoreCase = true) ||
                        item.date.contains(query, ignoreCase = true) ||
                        item.des.contains(query, ignoreCase = true)
            }
        }
        notLoist.clear()
        notLoist.addAll(filteredList)

        // اینجا لیست آیدی‌ها رو هم به روز می‌کنیم
        idList = filteredList.map { it.id }.toMutableList()
        Log.d("testList" , idList.toString())
        notifyDataSetChanged()
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

