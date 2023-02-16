package com.kodegakure.ta.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.kodegakure.ta.DashboardActivity
import com.kodegakure.ta.R

class HasilPresensiActivity : AppCompatActivity() {
    private lateinit var hasilPresensiTextView: TextView
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_presensi)

        val message: String = intent.getStringExtra("message").toString()
        hasilPresensiTextView = findViewById(R.id.textViewHasilPresensi)
        hasilPresensiTextView.text = message

        button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}