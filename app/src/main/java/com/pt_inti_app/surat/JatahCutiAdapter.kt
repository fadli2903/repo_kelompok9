package com.pt_inti_app.surat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pt_inti_app.R
import com.pt_inti_app.model.JatahCuti

class JatahCutiAdapter(private val data: List<JatahCuti>) :
    RecyclerView.Adapter<JatahCutiAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvJatah: TextView = view.findViewById(R.id.tvJatah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jatah_cuti, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jatah = data[position]
        holder.tvNama.text = jatah.karyawanNama.ifEmpty { jatah.karyawanId }
        holder.tvJatah.text = "Terpakai: ${jatah.terpakai} / ${jatah.totalJatah} hari — Sisa: ${jatah.sisa} hari"
    }

    override fun getItemCount() = data.size
}