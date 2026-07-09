package com.pt_inti_app.model

// Field 'role' menampung: "KARYAWAN", "ATASAN", "ANAK_MAGANG", "TIM_HELPDESK", "SDM"
data class User(
    val uid: String = "",
    val username: String = "",
    val role: String = ""
)