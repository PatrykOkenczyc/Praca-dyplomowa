package com.example.reflexapp.ui

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.reflexapp.R
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var etPasswordRepeat: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var btnBack: ImageView

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etPassword = findViewById(R.id.etPassword)
        etPasswordRepeat = findViewById(R.id.etPasswordRepeat)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnBack = findViewById(R.id.btnBack)


        btnBack.setOnClickListener { finish() }

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

        val email = intent.getStringExtra("email")
        if (email == null) {
            Toast.makeText(this, "Brak adresu email!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnResetPassword.setOnClickListener {

            val pass1 = etPassword.text.toString()
            val pass2 = etPasswordRepeat.text.toString()

            if (pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Uzupełnij wszystkie pola!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                Toast.makeText(this, "Hasła nie są identyczne!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            resetPassword(email!!, pass1)
        }
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

    private fun resetPassword(email: String, password: String) {
        val url = "http://192.168.0.216/reflexapp/reset_password.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            { response ->
                val json = JSONObject(response.trim())
                if (json.getString("status") == "success") {

                    Toast.makeText(this, "Hasło zostało zmienione!", Toast.LENGTH_LONG).show()
                    finish()

                } else {
                    Toast.makeText(this, "Błąd serwera!", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Błąd połączenia", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf(
                    "email" to email,
                    "password" to password
                )
        }

        queue.add(request)
    }
}
