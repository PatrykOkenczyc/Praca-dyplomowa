package com.example.reflexapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reflexapp.R

class ResultsSingleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_result)

        val backBtn = findViewById<ImageView>(R.id.btnBack)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val wynik = intent.getStringExtra("wynik") ?: ""

        val parts = wynik.split(",")

        val avg = parts.getOrNull(0) ?: "-"
        val min = parts.getOrNull(1) ?: "-"
        val max = parts.getOrNull(2) ?: "-"

        val textAvg = findViewById<TextView>(R.id.textAvg)
        val textMin = findViewById<TextView>(R.id.textMin)
        val textMax = findViewById<TextView>(R.id.textMax)

        textAvg.text = "$avg ms"
        textMin.text = "$min ms"
        textMax.text = "$max ms"
    }
}