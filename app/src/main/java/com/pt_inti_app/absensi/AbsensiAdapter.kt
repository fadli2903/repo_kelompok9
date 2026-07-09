package com.pt_inti_app.absensi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pt_inti_app.R
import com.pt_inti_app.model.Absensi
import java.text.SimpleDateFormat
import java.util.*

class AbsensiAdapter(private val data: List<Absensi>) :
    RecyclerView.Adapter<AbsensiAdapter.ViewHolder>() {

    private val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id"))

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val tvCheckIn: android.widget.TextView = view.findViewById(R.id.tvCheckIn)
        val tvCheckOut: android.widget.TextView = view.findViewById(R.id.tvCheckOut)
        val tvJamKerja: android.widget.TextView = view.findViewById(R.id.tvJamKerja)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_absensi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val absensi = data[position]
        holder.tvCheckIn.text = "Check In: ${sdf.format(Date(absensi.waktuCheckIn))}"
        holder.tvCheckOut.text = if (absensi.waktuCheckOut != null)
            "Check Out: ${sdf.format(Date(absensi.waktuCheckOut))}" else "Check Out: -"
        holder.tvJamKerja.text = "Jam kerja: ${absensi.hitungJamKerja()} jam"
    }

    override fun getItemCount() = data.size
}