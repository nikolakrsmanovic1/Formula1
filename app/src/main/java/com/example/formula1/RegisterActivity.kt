package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        applyInsets()

        // Strelica nazad i link "Prijavi se" -> vraća na Prijavu
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.tvHaveAccount).setOnClickListener { finish() }

        // Registruj se -> provera polja pa Početna
        findViewById<View>(R.id.btnRegister).setOnClickListener {
            if (validate()) {
                Toast.makeText(this, R.string.toast_registered, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    /** Proverava polja i prikazuje poruku uz polje koje nije u redu. */
    private fun validate(): Boolean {
        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirm = findViewById<EditText>(R.id.etConfirm)
        var ok = true

        for (field in listOf(name, email)) {
            field.error = null
            if (field.text.isNullOrBlank()) {
                field.error = getString(R.string.error_required)
                ok = false
            }
        }

        password.error = null
        if (password.text.length < resources.getInteger(R.integer.min_password_length)) {
            password.error = getString(R.string.error_password_short)
            ok = false
        }

        confirm.error = null
        if (confirm.text.toString() != password.text.toString()) {
            confirm.error = getString(R.string.error_password_mismatch)
            ok = false
        }
        return ok
    }
}
