package com.pt_inti_app.helpdesk

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pt_inti_app.R
import com.pt_inti_app.data.FirebaseRepository
import kotlinx.coroutines.launch

class HelpdeskListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helpdesk_list)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvTiket)
        rv.layoutManager = LinearLayoutManager(this)

        fun muatData() {
            lifecycleScope.launch {
                val list = FirebaseRepository.getTiketBaru()
                rv.adapter = ProblemAdapter(list) { problem ->
                    lifecycleScope.launch {
                        FirebaseRepository.konfirmasiPenyelesaian(
                            problem.tiketId, "Sudah ditangani oleh tim IT"
                        )
                        muatData()
                    }
                }
            }
        }
        muatData()
    }
}