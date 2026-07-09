package com.pt_inti_app.model

data class JatahCuti(
    val karyawanId: String = "",
    val karyawanNama: String = "",
    val tahun: Int = 2026,
    val totalJatah: Int = 12,
    val terpakai: Int = 0
) {
    val sisa: Int get() = totalJatah - terpakai
}