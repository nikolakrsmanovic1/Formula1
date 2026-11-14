package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)

        // Strelica nazad i link "Prijavi se" -> vraća na Prijavu
        binding.btnBack.setOnClickListener { finish() }
        binding.tvHaveAccount.setOnClickListener { finish() }

        // Registruj se -> provera polja pa Početna
        binding.btnRegister.setOnClickListener {
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
        var ok = true

        for (field in listOf(binding.etName, binding.etEmail)) {
            field.error = null
            if (field.text.isNullOrBlank()) {
                field.error = getString(R.string.error_required)
                ok = false
            }
        }

        binding.etPassword.error = null
        if (binding.etPassword.text.length < resources.getInteger(R.integer.min_password_length)) {
            binding.etPassword.error = getString(R.string.error_password_short)
            ok = false
        }

        binding.etConfirm.error = null
        if (binding.etConfirm.text.toString() != binding.etPassword.text.toString()) {
            binding.etConfirm.error = getString(R.string.error_password_mismatch)
            ok = false
        }
        return ok
    }
}
