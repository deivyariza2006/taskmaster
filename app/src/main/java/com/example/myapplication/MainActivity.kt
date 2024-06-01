package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val isRegistered = sharedPref.getBoolean("isRegistered", false)

        val intent = if (isRegistered) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, SignUpActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}

