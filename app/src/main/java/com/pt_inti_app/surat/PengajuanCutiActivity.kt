package com.pt_inti_app.surat

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import com.pt_inti_app.model.SuratPengajuan
import kotlinx.coroutines.launch

class PengajuanCutiActivity : AppCompatActivity() {

    private val jenisList = listOf("Cuti Tahunan", "Cuti Sakit", "Cuti Melahirkan", "Duka (Orang Terdekat Meninggal)", "Lainnya")
    private val jenisKode = listOf("TAHUNAN", "SAKIT", "MELAHIRKAN", "DUKA", "LAINNYA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengajuan_cuti)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val tvSisaJatah = findViewById<TextView>(R.id.tvSisaJatah)
        val spinnerJenis = findViewById<Spinner>(R.id.spinnerJenis)
        val tvInfoOtomatis = findViewById<TextView>(R.id.tvInfoOtomatis)
        val layoutJumlahHari = findViewById<TextInputLayout>(R.id.layoutJumlahHari)
        val etJumlahHari = findViewById<TextInputEditText>(R.id.etJumlahHari)
        val layoutKeterangan = findViewById<TextInputLayout>(R.id.layoutKeterangan)
        val etKeterangan = findViewById<TextInputEditText>(R.id.etKeterangan)
        val cbKasusKhusus = findViewById<CheckBox>(R.id.cbKasusKhusus)
        val btnAjukan = findViewById<MaterialButton>(R.id.btnAjukan)

        spinnerJenis.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, jenisList)

        val uid = FirebaseRepository.currentUid
        if (uid == null) { finish(); return }

        var namaKaryawan = ""

        lifecycleScope.launch {
            val user = FirebaseRepository.getCurrentUserProfile()
            namaKaryawan = user?.username ?: ""
            val jatah = FirebaseRepository.getJatahCuti(uid, namaKaryawan)
            tvSisaJatah.text = "Sisa jatah cuti tahun ini: ${jatah.sisa} dari ${jatah.totalJatah} hari"
        }

        fun updateTampilan(posisi: Int) {
            val kode = jenisKode[posisi]
            layoutJumlahHari.visibility = View.GONE
            layoutKeterangan.visibility = View.GONE
            cbKasusKhusus.visibility = View.GONE
            tvInfoOtomatis.visibility = View.GONE

            when (kode) {
                "TAHUNAN", "SAKIT" -> layoutJumlahHari.visibility = View.VISIBLE
                "MELAHIRKAN" -> {
                    tvInfoOtomatis.visibility = View.VISIBLE
                    tvInfoOtomatis.text = "Otomatis: 90 hari (3 bulan) — tidak memotong jatah cuti tahunan"
                }
                "DUKA" -> {
                    tvInfoOtomatis.visibility = View.VISIBLE
                    tvInfoOtomatis.text = "Otomatis: 2 hari — tidak memotong jatah cuti tahunan"
                }
                "LAINNYA" -> {
                    layoutJumlahHari.visibility = View.VISIBLE
                    layoutKeterangan.visibility = View.VISIBLE
                    cbKasusKhusus.visibility = View.VISIBLE
                }
            }
        }

        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) = updateTampilan(pos)
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        updateTampilan(0)

        btnAjukan.setOnClickListener {
            val posisi = spinnerJenis.selectedItemPosition
            val kode = jenisKode[posisi]

            val jumlahHari: Int
            val potongJatah: Boolean
            val keterangan = etKeterangan.text.toString().trim()

            when (kode) {
                "TAHUNAN", "SAKIT" -> {
                    val input = etJumlahHari.text.toString().trim()
                    if (input.isEmpty() || input.toIntOrNull() == null || input.toInt() <= 0) {
                        Toast.makeText(this, "Isi jumlah hari dengan benar", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    jumlahHari = input.toInt()
                    potongJatah = true
                }
                "MELAHIRKAN" -> { jumlahHari = 90; potongJatah = false }
                "DUKA" -> { jumlahHari = 2; potongJatah = false }
                "LAINNYA" -> {
                    val input = etJumlahHari.text.toString().trim()
                    if (input.isEmpty() || input.toIntOrNull() == null || input.toInt() <= 0 || keterangan.isEmpty()) {
                        Toast.makeText(this, "Isi jumlah hari dan keterangan", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    jumlahHari = input.toInt()
                    potongJatah = !cbKasusKhusus.isChecked
                }
                else -> return@setOnClickListener
            }

            lifecycleScope.launch {
                val surat = SuratPengajuan(
                    karyawanId = uid,
                    karyawanNama = namaKaryawan,
                    kategori = "CUTI",
                    jenisCuti = kode,
                    keterangan = keterangan,
                    jumlahHari = jumlahHari,
                    potongJatah = potongJatah
                )
                val sukses = FirebaseRepository.ajukanSurat(surat)
                Toast.makeText(
                    this@PengajuanCutiActivity,
                    if (sukses) "Pengajuan cuti terkirim" else "Gagal mengajukan",
                    Toast.LENGTH_SHORT
                ).show()
                if (sukses) finish()
            }
        }
    }
}