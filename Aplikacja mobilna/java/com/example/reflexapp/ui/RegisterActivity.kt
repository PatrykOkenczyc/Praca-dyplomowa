package com.example.reflexapp.ui

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

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val backBtn = findViewById<ImageView>(R.id.btnBack)
        backBtn.setOnClickListener {
            finish()
        }

        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordRepeat = findViewById<EditText>(R.id.etPasswordRepeat)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val ruleLength = findViewById<TextView>(R.id.ruleLength)
        val ruleLower = findViewById<TextView>(R.id.ruleLower)
        val ruleUpper = findViewById<TextView>(R.id.ruleUpper)
        val ruleDigit = findViewById<TextView>(R.id.ruleDigit)
        val ruleSpecial = findViewById<TextView>(R.id.ruleSpecial)

        var isLengthOk = false
        var isLowerOk = false
        var isUpperOk = false
        var isDigitOk = false
        var isSpecialOk = false


        etPassword.addTextChangedListener { text ->
            val password = text.toString()


            isLengthOk = password.length >= 8
            isLowerOk = password.any { it.isLowerCase() }
            isUpperOk = password.any { it.isUpperCase() }
            isDigitOk = password.any { it.isDigit() }
            isSpecialOk = password.any { !it.isLetterOrDigit() }

            updateRuleIcon(ruleLength, isLengthOk)
            updateRuleIcon(ruleLower, isLowerOk)
            updateRuleIcon(ruleUpper, isUpperOk)
            updateRuleIcon(ruleDigit, isDigitOk)
            updateRuleIcon(ruleSpecial, isSpecialOk)
        }

        etPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= etPassword.right - etPassword.compoundPaddingEnd) {
                    togglePasswordVisibility(etPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }

        etPasswordRepeat.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= etPasswordRepeat.right - etPasswordRepeat.compoundPaddingEnd) {
                    togglePasswordVisibility(etPasswordRepeat)
                    return@setOnTouchListener true
                }
            }
            false
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val passwordRepeat = etPasswordRepeat.text.toString()

            val allValid = isLengthOk && isLowerOk && isUpperOk && isDigitOk && isSpecialOk

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Nieprawidłowy email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!allValid) {
                Toast.makeText(this, "Hasło nie spełnia wymagań!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != passwordRepeat) {
                Toast.makeText(this, "Hasła nie są identyczne!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            registerUser(email, password)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun updateRuleIcon(rule: TextView, isOk: Boolean) {
        val icon = if (isOk) R.drawable.valid else R.drawable.invalid
        rule.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    }

    private fun togglePasswordVisibility(editText: EditText) {
        val isPasswordVisible =
            editText.transformationMethod !is PasswordTransformationMethod

        if (isPasswordVisible) {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0)
        } else {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0)
        }

        editText.setSelection(editText.text.length)
    }

    private fun registerUser(email: String, password: String) {
        val url = "http://192.168.0.216/reflexapp/register.php"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                Log.e("REGISTER", "Server response: $response")

                try {
                    val json = JSONObject(response)
                    val status = json.getString("status")

                    when (status) {
                        "success" -> {
                            Toast.makeText(this, "Rejestracja zakończona!", Toast.LENGTH_LONG).show()
                            finish()
                        }
                        "exists" -> {
                            Toast.makeText(this, "Taki email już istnieje!", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this, "Błąd serwera!", Toast.LENGTH_LONG).show()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("REGISTER", "JSON parse error: ${e.message}")
                }
            },
            { error ->
                Log.e("REGISTER", "Volley error: ${error.message}")
                Toast.makeText(this, "Błąd połączenia: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        queue.add(stringRequest)
    }









}


