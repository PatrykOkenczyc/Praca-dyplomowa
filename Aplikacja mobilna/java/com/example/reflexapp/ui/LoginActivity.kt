package com.example.reflexapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reflexapp.R
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val backBtn = findViewById<ImageView>(R.id.btnBack)
        backBtn.setOnClickListener {
            finish()
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val resetPassword = findViewById<TextView>(R.id.resetPassword)


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Uzupełnij wszystkie pola!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Nieprawidłowy email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email,password)
        }

        resetPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loginUser(email: String, password: String) {

        val url = "http://192.168.0.216/reflexapp/login.php"
        val requestQueue = Volley.newRequestQueue(this)

        val request = object : StringRequest(
            Method.POST, url,
            { response ->

                Log.e("LOGIN", "Server response: $response")

                try {
                    val json = JSONObject(response.trim())
                    val status = json.getString("status")

                    when (status) {
                        "success" -> {
                            val userId = json.getInt("user_id")

                            val prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE)
                            prefs.edit().putInt("user_id", userId).apply()

                            Toast.makeText(this, "Zalogowano pomyślnie!", Toast.LENGTH_LONG).show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            val message = json.getString("message")
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("LOGIN", "JSON error: ${e.message}")
                    Toast.makeText(this, "Błąd odpowiedzi serwera!", Toast.LENGTH_LONG).show()
                }

            },
            { error ->
                Log.e("LOGIN", "Volley error: ${error.message}")
                Toast.makeText(this, "Błąd połączenia z serwerem!", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        requestQueue.add(request)
    }
}
