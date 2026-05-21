package com.example.reflexapp.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PasswordUtils {

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val input = password + salt
        val hash = messageDigest.digest(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}
