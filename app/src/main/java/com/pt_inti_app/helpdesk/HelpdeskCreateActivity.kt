package com.pt_inti_app.helpdesk

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import com.pt_inti_app.model.Problem
import kotlinx.coroutines.launch

class HelpdeskCreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helpdesk_create)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val etKategori = findViewById<TextInputEditText>(R.id.etKategori)
        val etDeskripsi = findViewById<TextInputEditText>(R.id.etDeskripsi)
        val btnKirim = findViewById<MaterialButton>(R.id.btnKirimTiket)

        val uid = FirebaseRepository.currentUid
        if (uid == null) {
            Toast.makeText(this, "Sesi login tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnKirim.setOnClickListener {
            val kategori = etKategori.text.toString().trim()
            val deskripsi = etDeskripsi.text.toString().trim()
            if (kategori.isEmpty() || deskripsi.isEmpty()) {
                Toast.makeText(this, "Lengkapi form dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val problem = Problem(
                    karyawanId = uid,
                    kategoriMasalah = kategori,
                    deskripsiMasalah = deskripsi
                )
                val sukses = FirebaseRepository.buatTiketProblem(problem)
                Toast.makeText(
                    this@HelpdeskCreateActivity,
                    if (sukses) "Tiket berhasil dikirim" else "Gagal mengirim tiket",
                    Toast.LENGTH_SHORT
                ).show()
                if (sukses) finish()
            }
        }
    }
}