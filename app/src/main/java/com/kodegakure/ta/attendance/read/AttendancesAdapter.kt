package com.kodegakure.ta.attendance.read

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kodegakure.ta.R
import com.kodegakure.ta.model.response.RiwayatPresensiResponse
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class AttendancesAdapter(private val attendances: ArrayList<RiwayatPresensiResponse>) :
    RecyclerView.Adapter<AttendancesAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = attendances[position]

        if (currentItem.lembur == null){
            holder.lembur.text = "-"
        } else {
            holder.lembur.text = currentItem.lembur.toString() + " jam"
        }

        holder.tanggal.text = formatDate(currentItem.jam_masuk!!)
        holder.jamMasuk.text = formatTime(currentItem.jam_masuk)
        holder.jamPulang.text = currentItem.jam_pulang?.let { formatTime(it) }
        holder.status.text = currentItem.status
    }

    override fun getItemCount(): Int {
        return attendances.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggal: TextView = itemView.findViewById(R.id.tanggal_presensi)
        val jamMasuk: TextView = itemView.findViewById(R.id.jam_masuk)
        val jamPulang: TextView = itemView.findViewById(R.id.jam_pulang)
        val lembur: TextView = itemView.findViewById(R.id.lembur)
        val status: TextView = itemView.findViewById(R.id.status_kehadiran)
    }

    fun setData(data: ArrayList<RiwayatPresensiResponse>) {
        attendances.clear()
        attendances.addAll(data)
        notifyDataSetChanged()
    }

    private fun formatDate(date: String): String {
        val f: Format = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val inputFormat = SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.ROOT)
        val hasBeenFormatted = inputFormat.parse(date)
        return f.format(hasBeenFormatted)
    }

    private fun formatTime(date: String): String {
        val f: Format = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        val inputFormat = SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.ROOT)
        val hasBeenFormatted = inputFormat.parse(date)
        return f.format(hasBeenFormatted)
    }
}