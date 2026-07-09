package com.pt_inti_app.model

data class Absensi(
    val absensiId: String = "",
    val karyawanId: String = "",
    val waktuCheckIn: Long = 0L,      // disimpan sebagai timestamp millis
    val waktuCheckOut: Long? = null,
    val statusKehadiran: String = "HADIR"
) {
    // method hitungJamKerja() dari diagram
    fun hitungJamKerja(): Float {
        if (waktuCheckOut == null) return 0f
        val selisihMs = waktuCheckOut - waktuCheckIn
        return selisihMs / 1000f / 60f / 60f // konversi ke jam
    }
}