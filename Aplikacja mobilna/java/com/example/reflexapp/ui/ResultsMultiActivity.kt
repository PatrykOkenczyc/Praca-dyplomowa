package com.example.reflexapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reflexapp.R

class ResultsMultiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_result)

        val backBtn = findViewById<ImageView>(R.id.btnBack)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val result = intent.getStringExtra("wynik") ?: ""
        val parts = result.split(",")

        if (parts.size < 6) return

        val avg1 = parts[0].toInt()
        val min1 = parts[1]
        val max1 = parts[2]

        val avg2 = parts[3].toInt()
        val min2 = parts[4]
        val max2 = parts[5]

        val avgLeft = findViewById<TextView>(R.id.textAvgLeft)
        val minLeft = findViewById<TextView>(R.id.textMinLeft)
        val maxLeft = findViewById<TextView>(R.id.textMaxLeft)

        val avgRight = findViewById<TextView>(R.id.textAvgRight)
        val minRight = findViewById<TextView>(R.id.textMinRight)
        val maxRight = findViewById<TextView>(R.id.textMaxRight)

        avgLeft.text = avg1.toString()
        minLeft.text = min1
        maxLeft.text = max1

        avgRight.text = avg2.toString()
        minRight.text = min2
        maxRight.text = max2


        if (avg1 < avg2) {
            avgLeft.setTextColor(getColor(R.color.primary))
            avgRight.setTextColor(getColor(R.color.textSecondary))
        } else {
            avgRight.setTextColor(getColor(R.color.primary))
            avgLeft.setTextColor(getColor(R.color.textSecondary))
        }

    }
}