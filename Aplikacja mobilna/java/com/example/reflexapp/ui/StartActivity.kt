package com.example.reflexapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reflexapp.R
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class StartActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var loading: ProgressBar


    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        statusText = findViewById(R.id.statusText)
        loading = findViewById(R.id.loading)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.isEnabled = false
        btnRegister.isEnabled = false

        testConnection()

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun testConnection() {
        val url = "http://192.168.0.216/reflexapp/test_connection.php"

        statusText.text = "Łączenie z serwerem..."
        loading.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        btnRegister.isEnabled = false

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("SERVER", "Response: $response")

                if (response.contains("ok", ignoreCase = true)) {
                    statusText.text = "Połączono z serwerem"
                    loading.visibility = View.GONE
                    btnLogin.isEnabled = true
                    btnRegister.isEnabled = true
                } else {
                    statusText.text = "Błędna odpowiedź serwera"
                    loading.visibility = View.GONE
                    Toast.makeText(this, "Odpowiedź: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.e("SERVER", "Error: $error")
                statusText.text = "Brak połączenia z serwerem"
                loading.visibility = View.GONE
                Toast.makeText(this, "Błąd połączenia z serwerem", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
