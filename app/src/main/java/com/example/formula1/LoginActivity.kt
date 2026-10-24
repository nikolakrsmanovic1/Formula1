package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        applyInsets()

        // Prijavi se -> Početna
        findViewById<android.view.View>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Registracija još nije napravljena
        findViewById<android.view.View>(R.id.tvRegister).setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }
    }
}
