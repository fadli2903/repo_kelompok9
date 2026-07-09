package com.pt_inti_app.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.pt_inti_app.LoginActivity
import com.pt_inti_app.R
import com.pt_inti_app.absensi.AbsensiActivity
import com.pt_inti_app.data.FirebaseRepository
import com.pt_inti_app.helpdesk.HelpdeskCreateActivity
import com.pt_inti_app.helpdesk.HelpdeskListActivity
import com.pt_inti_app.surat.DataCutiSdmActivity
import com.pt_inti_app.surat.SuratApprovalActivity
import com.pt_inti_app.surat.SuratListActivity
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvSuratLabel = findViewById<TextView>(R.id.tvSuratLabel)
        val cardAbsensi = findViewById<MaterialCardView>(R.id.btnAbsensi)
        val cardSurat = findViewById<MaterialCardView>(R.id.btnSurat)
        val cardHelpdesk = findViewById<MaterialCardView>(R.id.btnHelpdesk)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        cardAbsensi.setOnClickListener { startActivity(Intent(this, AbsensiActivity::class.java)) }
        btnLogout.setOnClickListener {
            FirebaseRepository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            val user = FirebaseRepository.getCurrentUserProfile()
            if (user == null) {
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
                return@launch
            }
            tvWelcome.text = "${user.username} (${user.role})"

            when (user.role) {
                "TIM_HELPDESK" -> {
                    cardAbsensi.visibility = android.view.View.GONE
                    cardSurat.visibility = android.view.View.GONE
                    cardHelpdesk.setOnClickListener {
                        startActivity(Intent(this@DashboardActivity, HelpdeskListActivity::class.java))
                    }
                }
                "ATASAN" -> {
                    cardAbsensi.visibility = android.view.View.GONE
                    cardHelpdesk.visibility = android.view.View.GONE
                    tvSuratLabel.text = "Approval Pengajuan"
                    cardSurat.setOnClickListener {
                        startActivity(Intent(this@DashboardActivity, SuratApprovalActivity::class.java))
                    }
                }
                "SDM" -> {
                    cardAbsensi.visibility = android.view.View.GONE
                    cardHelpdesk.visibility = android.view.View.GONE
                    tvSuratLabel.text = "Data Cuti Karyawan"
                    cardSurat.setOnClickListener {
                        startActivity(Intent(this@DashboardActivity, DataCutiSdmActivity::class.java))
                    }
                }
                else -> { // KARYAWAN & ANAK_MAGANG
                    cardSurat.setOnClickListener {
                        startActivity(Intent(this@DashboardActivity, SuratListActivity::class.java))
                    }
                    cardHelpdesk.setOnClickListener {
                        startActivity(Intent(this@DashboardActivity, HelpdeskCreateActivity::class.java))
                    }
                }
            }
        }
    }
}