package com.pt_inti_app.surat

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import com.pt_inti_app.model.SuratPengajuan
import kotlinx.coroutines.launch

class PengajuanLemburActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengajuan_lembur)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val etJumlahJam = findViewById<TextInputEditText>(R.id.etJumlahJam)
        val etKeterangan = findViewById<TextInputEditText>(R.id.etKeterangan)
        val btnAjukan = findViewById<MaterialButton>(R.id.btnAjukan)

        val uid = FirebaseRepository.currentUid
        if (uid == null) { finish(); return }

        btnAjukan.setOnClickListener {
            val jam = etJumlahJam.text.toString().trim()
            val keterangan = etKeterangan.text.toString().trim()

            if (jam.isEmpty() || jam.toIntOrNull() == null || jam.toInt() <= 0 || keterangan.isEmpty()) {
                Toast.makeText(this, "Lengkapi form dengan benar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = FirebaseRepository.getCurrentUserProfile()
                val surat = SuratPengajuan(
                    karyawanId = uid,
                    karyawanNama = user?.username ?: "",
                    kategori = "LEMBUR",
                    keterangan = keterangan,
                    jumlahJam = jam.toInt(),
                    potongJatah = false
                )
                val sukses = FirebaseRepository.ajukanSurat(surat)
                Toast.makeText(
                    this@PengajuanLemburActivity,
                    if (sukses) "Pengajuan lembur terkirim" else "Gagal mengajukan",
                    Toast.LENGTH_SHORT
                ).show()
                if (sukses) finish()
            }
        }
    }
}