package com.pt_inti_app.surat

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pt_inti_app.R
import com.pt_inti_app.model.SuratPengajuan

class SuratAdapter(private val data: List<SuratPengajuan>) :
    RecyclerView.Adapter<SuratAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val iconWrapper: FrameLayout = view.findViewById(R.id.iconWrapper)
        val tvIcon: TextView = view.findViewById(R.id.tvIcon)
        val tvJenis: TextView = view.findViewById(R.id.tvJenis)
        val tvDetail: TextView = view.findViewById(R.id.tvDetail)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_surat, parent, false)
        return ViewHolder(view)
    }

    private fun labelJenis(kode: String) = when (kode) {
        "TAHUNAN" -> "Cuti Tahunan"
        "SAKIT" -> "Cuti Sakit"
        "MELAHIRKAN" -> "Cuti Melahirkan"
        "DUKA" -> "Duka"
        "LAINNYA" -> "Cuti Lainnya"
        else -> "Cuti"
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = data[position]

        if (surat.kategori == "LEMBUR") {
            holder.tvIcon.text = "⏱️"
            holder.iconWrapper.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.icon_bg_amber)
            holder.tvJenis.text = "Lembur"
            holder.tvDetail.text = "${surat.jumlahJam} jam — ${surat.keterangan}"
        } else {
            holder.tvIcon.text = "📄"
            holder.iconWrapper.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.icon_bg_teal)
            holder.tvJenis.text = labelJenis(surat.jenisCuti)
            holder.tvDetail.text = "${surat.jumlahHari} hari" + if (!surat.potongJatah) " · tidak potong jatah" else ""
        }

        holder.tvStatus.text = surat.statusApproval
        val warna = when (surat.statusApproval) {
            "DISETUJUI" -> R.color.status_approved
            "DITOLAK" -> R.color.status_rejected
            else -> R.color.status_pending
        }
        val bg = holder.tvStatus.background.mutate() as GradientDrawable
        bg.setColor(ContextCompat.getColor(holder.itemView.context, warna))
    }

    override fun getItemCount() = data.size
}