package com.pt_inti_app.surat

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pt_inti_app.R
import com.pt_inti_app.model.SuratPengajuan

class SuratApprovalAdapter(
    private val data: List<SuratPengajuan>,
    private val readOnly: Boolean = false,
    private val onSetuju: (SuratPengajuan) -> Unit = {},
    private val onTolak: (SuratPengajuan) -> Unit = {}
) : RecyclerView.Adapter<SuratApprovalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJenis: TextView = view.findViewById(R.id.tvJenis)
        val tvDetail: TextView = view.findViewById(R.id.tvDetail)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvKaryawan: TextView = view.findViewById(R.id.tvKaryawan)
        val layoutAksi: View = view.findViewById(R.id.layoutAksi)
        val btnSetuju: MaterialButton = view.findViewById(R.id.btnSetuju)
        val btnTolak: MaterialButton = view.findViewById(R.id.btnTolak)
    }

    private fun labelJenis(surat: SuratPengajuan): String {
        if (surat.kategori == "LEMBUR") return "Lembur"
        return when (surat.jenisCuti) {
            "TAHUNAN" -> "Cuti Tahunan"
            "SAKIT" -> "Cuti Sakit"
            "MELAHIRKAN" -> "Cuti Melahirkan"
            "DUKA" -> "Duka"
            "LAINNYA" -> "Cuti Lainnya"
            else -> "Cuti"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_surat_approval, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = data[position]
        holder.tvJenis.text = labelJenis(surat)
        holder.tvKaryawan.text = "Diajukan oleh: ${surat.karyawanNama.ifEmpty { surat.karyawanId.take(8) }}"
        holder.tvDetail.text = if (surat.kategori == "LEMBUR")
            "${surat.jumlahJam} jam — ${surat.keterangan}"
        else
            "${surat.jumlahHari} hari" + if (!surat.potongJatah) " · tidak potong jatah" else ""

        holder.tvStatus.text = surat.statusApproval
        val warna = when (surat.statusApproval) {
            "DISETUJUI" -> R.color.status_approved
            "DITOLAK" -> R.color.status_rejected
            else -> R.color.status_pending
        }
        val bg = holder.tvStatus.background.mutate() as GradientDrawable
        bg.setColor(ContextCompat.getColor(holder.itemView.context, warna))

        holder.layoutAksi.visibility =
            if (!readOnly && surat.statusApproval == "MENUNGGU") View.VISIBLE else View.GONE

        holder.btnSetuju.setOnClickListener { onSetuju(surat) }
        holder.btnTolak.setOnClickListener { onTolak(surat) }
    }

    override fun getItemCount() = data.size
}