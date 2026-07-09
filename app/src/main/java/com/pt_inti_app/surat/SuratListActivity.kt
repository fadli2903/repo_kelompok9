package com.pt_inti_app.surat

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import kotlinx.coroutines.launch

class SuratListActivity : AppCompatActivity() {

    private lateinit var rvSurat: RecyclerView
    private lateinit var tvSisaJatah: TextView
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_list)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        tvSisaJatah = findViewById(R.id.tvSisaJatah)
        rvSurat = findViewById(R.id.rvSurat)
        rvSurat.layoutManager = LinearLayoutManager(this)

        uid = FirebaseRepository.currentUid
        if (uid == null) { finish(); return }

        findViewById<MaterialButton>(R.id.btnAjukanCuti).setOnClickListener {
            startActivity(Intent(this, PengajuanCutiActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnAjukanLembur).setOnClickListener {
            startActivity(Intent(this, PengajuanLemburActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        muatData() // reload tiap kembali ke layar ini, biar data terbaru
    }

    private fun muatData() {
        val id = uid ?: return
        lifecycleScope.launch {
            val user = FirebaseRepository.getCurrentUserProfile()
            val jatah = FirebaseRepository.getJatahCuti(id, user?.username ?: "")
            tvSisaJatah.text = "Sisa jatah cuti tahun ini: ${jatah.sisa} dari ${jatah.totalJatah} hari"

            val list = FirebaseRepository.getSuratByKaryawan(id)
                .sortedByDescending { it.tanggalAjuan }
            rvSurat.adapter = SuratAdapter(list)
        }
    }
}