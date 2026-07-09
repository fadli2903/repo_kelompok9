package com.pt_inti_app.model

data class SuratPengajuan(
    val suratId: String = "",
    val karyawanId: String = "",
    val karyawanNama: String = "",
    val kategori: String = "CUTI",       // "CUTI" atau "LEMBUR"
    val jenisCuti: String = "",          // TAHUNAN | SAKIT | MELAHIRKAN | DUKA | LAINNYA (kosong jika LEMBUR)
    val keterangan: String = "",         // alasan tambahan / alasan lembur
    val jumlahHari: Int = 0,             // dipakai untuk CUTI
    val jumlahJam: Int = 0,              // dipakai untuk LEMBUR
    val tanggalMulai: Long = System.currentTimeMillis(),
    val potongJatah: Boolean = true,     // false untuk melahirkan/duka/kasus khusus
    val statusApproval: String = "MENUNGGU",
    val tanggalAjuan: Long = System.currentTimeMillis()
)