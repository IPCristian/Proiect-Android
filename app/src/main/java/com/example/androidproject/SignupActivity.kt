package com.example.androidproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.androidproject.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val userDatabase by lazy { UserDatabase.getDatabase(this).userDao() }
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE)

        binding.loginText2.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signupButton.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            val displayname = binding.editTextTextDisplayName.text.toString()
            val crtUser = userDatabase.getUserByEmail(email)
            if (crtUser?.email.equals(email))
            {
                Toast.makeText(applicationContext,"A user already exists with this email", Toast.LENGTH_SHORT).show()
            }
            else if (email.equals("") || password.equals("") || displayname.equals(""))
            {
                Toast.makeText(applicationContext,"One of the fields is empty", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val newUser = User(displayname,email,password)
                userDatabase.insert(newUser)
                val editor = preferences.edit()
                editor.putString("name", newUser.name)
                editor.apply()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}