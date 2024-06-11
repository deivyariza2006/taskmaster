package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val signUpButton = findViewById<Button>(R.id.buttonSignUp)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()



            val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("username", username)
                putString("password", password)
                putBoolean("isRegistered", true)
                apply()

            }

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() }
                else {
                    Toast.makeText(this, "Neither the username nor the password can be empty", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }





