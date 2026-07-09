package com.pt_inti_app.model

data class AktivitasHarian(
    val aktivitasId: String = "",
    val karyawanId: String = "",
    val tanggal: Long = System.currentTimeMillis(),
    val deskripsi: String = ""
)