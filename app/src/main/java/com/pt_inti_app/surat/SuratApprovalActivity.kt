package com.pt_inti_app.surat

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import kotlinx.coroutines.launch

class SuratApprovalActivity : AppCompatActivity() {

    companion object { const val EXTRA_READ_ONLY = "read_only" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_approval)

        val readOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, false)
        if (readOnly) {
            findViewById<TextView>(R.id.tvHeaderTitle).text = "Riwayat Semua Pengajuan"
        }

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvApproval)
        rv.layoutManager = LinearLayoutManager(this)

        fun muatData() {
            lifecycleScope.launch {
                val list = FirebaseRepository.getSemuaSurat()
                    .sortedBy { it.statusApproval != "MENUNGGU" }
                rv.adapter = SuratApprovalAdapter(
                    data = list,
                    readOnly = readOnly,
                    onSetuju = { surat ->
                        lifecycleScope.launch {
                            val sukses = FirebaseRepository.setujuiSurat(surat)
                            Toast.makeText(
                                this@SuratApprovalActivity,
                                if (sukses) "Pengajuan disetujui" else "Gagal update status",
                                Toast.LENGTH_SHORT
                            ).show()
                            muatData()
                        }
                    },
                    onTolak = { surat ->
                        lifecycleScope.launch {
                            val sukses = FirebaseRepository.tolakSurat(surat.suratId)
                            Toast.makeText(
                                this@SuratApprovalActivity,
                                if (sukses) "Pengajuan ditolak" else "Gagal update status",
                                Toast.LENGTH_SHORT
                            ).show()
                            muatData()
                        }
                    }
                )
            }
        }
        muatData()
    }
}