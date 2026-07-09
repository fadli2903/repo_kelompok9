package com.pt_inti_app.model

data class Problem(
    val tiketId: String = "",
    val karyawanId: String = "",
    val kategoriMasalah: String = "",
    val deskripsiMasalah: String = "",
    val statusTiket: String = "BARU", // BARU / DIPROSES / SELESAI
    val waktuDibuat: Long = System.currentTimeMillis(),
    val waktuSelesai: Long? = null,
    val catatanPenyelesaian: String = ""
)