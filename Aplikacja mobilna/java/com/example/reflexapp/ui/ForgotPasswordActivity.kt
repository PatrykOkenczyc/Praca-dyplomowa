package com.example.reflexapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reflexapp.R
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnReset: Button
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etEmail = findViewById(R.id.etEmail)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        btnReset.setOnClickListener {

            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Podaj adres email!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            forgotPassword(email)
        }
    }

    private fun forgotPassword(email: String) {

        val url = "http://192.168.0.216/reflexapp/forgot_password.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            { response ->
                Log.e("FORGOT", "Server response: $response")

                try {
                    val json = JSONObject(response.trim())
                    val status = json.getString("status")

                    if (status == "success") {
                        Toast.makeText(this, "Kod został wysłany na email!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this, VerifyCodeActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "Błąd serwera!", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Log.e("FORGOT", "JSON error: ${e.message}")
                }
            },
            { error ->
                Log.e("FORGOT", "Network error: $error")
                Toast.makeText(this, "Błąd sieci!", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf("email" to email)
        }

        queue.add(request)
    }
}
