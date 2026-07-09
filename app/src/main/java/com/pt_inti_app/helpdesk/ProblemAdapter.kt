package com.pt_inti_app.helpdesk

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pt_inti_app.R
import com.pt_inti_app.model.Problem

class ProblemAdapter(
    private val data: List<Problem>,
    private val onSelesaikan: (Problem) -> Unit
) : RecyclerView.Adapter<ProblemAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val tvKategori: TextView = view.findViewById(R.id.tvKategori)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDeskripsi)
        val btnSelesaikan: Button = view.findViewById(R.id.btnSelesaikan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_problem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val problem = data[position]
        holder.tvKategori.text = "${problem.kategoriMasalah} (${problem.statusTiket})"
        holder.tvDeskripsi.text = problem.deskripsiMasalah
        holder.btnSelesaikan.setOnClickListener { onSelesaikan(problem) }
    }

    override fun getItemCount() = data.size
}