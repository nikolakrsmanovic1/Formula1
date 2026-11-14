package com.example.formula1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)

        // Prijavi se -> Početna
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Nemaš nalog? Registruj se -> Registracija
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
