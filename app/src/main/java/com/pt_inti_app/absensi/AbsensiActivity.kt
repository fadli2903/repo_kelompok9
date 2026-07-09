package com.pt_inti_app.absensi

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import kotlinx.coroutines.launch

class AbsensiActivity : AppCompatActivity() {

    private var absensiAktifId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val btnClockIn = findViewById<MaterialButton>(R.id.btnClockIn)
        val btnClockOut = findViewById<MaterialButton>(R.id.btnClockOut)
        val rvRiwayat = findViewById<RecyclerView>(R.id.rvRiwayat)
        rvRiwayat.layoutManager = LinearLayoutManager(this)

        val uid = FirebaseRepository.currentUid
        if (uid == null) {
            Toast.makeText(this, "Sesi login tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fun muatRiwayat() {
            lifecycleScope.launch {
                val list = FirebaseRepository.getRiwayatAbsensi(uid)
                rvRiwayat.adapter = AbsensiAdapter(list)
                absensiAktifId = list.lastOrNull { it.waktuCheckOut == null }?.absensiId
            }
        }

        btnClockIn.setOnClickListener {
            lifecycleScope.launch {
                val sukses = FirebaseRepository.clockIn(uid)
                Toast.makeText(
                    this@AbsensiActivity,
                    if (sukses) "Clock in berhasil" else "Gagal clock in",
                    Toast.LENGTH_SHORT
                ).show()
                muatRiwayat()
            }
        }

        btnClockOut.setOnClickListener {
            val id = absensiAktifId
            if (id == null) {
                Toast.makeText(this, "Belum ada clock in aktif", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val sukses = FirebaseRepository.clockOut(id)
                Toast.makeText(
                    this@AbsensiActivity,
                    if (sukses) "Clock out berhasil" else "Gagal clock out",
                    Toast.LENGTH_SHORT
                ).show()
                muatRiwayat()
            }
        }

        muatRiwayat()
    }
}