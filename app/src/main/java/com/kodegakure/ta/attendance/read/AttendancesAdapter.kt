package com.kodegakure.ta.attendance.read

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kodegakure.ta.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AttendancesAdapter(private val attendances: ArrayList<AttendancesResponse>) :
    RecyclerView.Adapter<AttendancesAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = attendances[position]
        holder.created_at.text = currentItem.diffForHuman
        holder.status_attendance.text = currentItem.status.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        if (currentItem.status == "alpha"){
            holder.status_attendance.setTextColor(Color.parseColor("#ef4444"))
        }

        if (currentItem.status == "hadir"){
            holder.status_attendance.setTextColor(Color.parseColor("#16a34a"))
        }

        if (currentItem.status == "izin"){
            holder.status_attendance.setTextColor(Color.parseColor("#ca8a04"))
        }
    }

    override fun getItemCount(): Int {
        return attendances.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val created_at: TextView = itemView.findViewById(R.id.created_at_attendance)
        val status_attendance: TextView = itemView.findViewById(R.id.status_attendance)
    }

    fun setData(data: ArrayList<AttendancesResponse>) {
        attendances.clear()
        attendances.addAll(data)
        notifyDataSetChanged()
    }
}