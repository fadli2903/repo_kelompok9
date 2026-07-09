package com.pt_inti_app.surat

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import kotlinx.coroutines.launch

class DataCutiSdmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_cuti_sdm)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvJatahCuti)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<MaterialButton>(R.id.btnLihatPengajuan).setOnClickListener {
            val intent = Intent(this, SuratApprovalActivity::class.java)
            intent.putExtra(SuratApprovalActivity.EXTRA_READ_ONLY, true)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val list = FirebaseRepository.getSemuaJatahCutiTahunIni().sortedBy { it.karyawanNama }
            rv.adapter = JatahCutiAdapter(list)
        }
    }
}