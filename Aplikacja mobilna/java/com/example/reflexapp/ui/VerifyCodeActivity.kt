package com.example.reflexapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reflexapp.R
import com.example.reflexapp.utils.PasswordUtils
import org.json.JSONObject




class VerifyCodeActivity : AppCompatActivity() {

    private lateinit var etCode: EditText
    private lateinit var btnVerify: Button
    private lateinit var email: String
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_code)



        etCode = findViewById(R.id.etCode)
        btnVerify = findViewById(R.id.btnVerify)

        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        email = intent.getStringExtra("email") ?: ""

        btnVerify.setOnClickListener {
            val code = etCode.text.toString()

            if (code.length != 6) {
                Toast.makeText(this, "Kod musi mieć 6 cyfr!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            verifyCode(email, code)
        }
    }

    private fun verifyCode(email: String, code: String) {
        val url = "http://192.168.0.216/reflexapp/verify_code.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            { response ->
                val json = JSONObject(response.trim())
                if (json.getString("status") == "success") {

                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Kod niepoprawny", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Błąd sieci!", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf("email" to email, "code" to code)
        }

        queue.add(request)
    }
}
