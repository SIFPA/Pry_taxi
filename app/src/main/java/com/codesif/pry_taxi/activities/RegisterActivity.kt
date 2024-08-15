package com.codesif.pry_taxi.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codesif.pry_taxi.R
import com.codesif.pry_taxi.databinding.ActivityRegisterBinding
import com.codesif.pry_taxi.models.Client
import com.codesif.pry_taxi.providers.AuthProvider
import com.codesif.pry_taxi.providers.ClientProvider

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener { goToLogin() }
        binding.btnRegister.setOnClickListener { register() }

    }

    private fun register () {
        val name = binding.textFieldName.text.toString()
        val surnames = binding.textFieldSurnames.text.toString()
        val email = binding.textFieldEmail.text.toString()
        val phone = binding.textFieldPhone.text.toString()
        val password = binding.textFieldPassword.text.toString()
        val confirmPassword = binding.textFieldConfirmPassword.text.toString()

        if (isValidForm(name, surnames, email, phone, password, confirmPassword)) {
            authProvider.register(email, password).addOnCompleteListener() {
                if (it.isSuccessful) {
                    val client = Client (
                        id = authProvider.getId(),
                        name = name,
                        surnames = surnames,
                        email = email,
                        phone = phone
                    )
                    clientProvider.create(client).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_LONG).show()
                            goToMap()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Hubo un error almacenando los datos del usuario: ${it.exception.toString()}", Toast.LENGTH_LONG).show()
                            Log.d("FIREBASE", "Error: ${it.exception.toString()}")
                        }
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registro fallido: ${it.exception.toString()}", Toast.LENGTH_LONG).show()
                    Log.d("FIREBASE", "Error: ${it.exception.toString()}")
                }
            }
        }
    }

    private fun goToMap() {
        val i = Intent(this, MapActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun isValidForm(name: String, surnames: String, email: String, phone: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar tu nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if (surnames.isEmpty()) {
            if (email.isEmpty()) {
                Toast.makeText(this, "Debes ingresar tu apellido", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Debes ingresar tu correo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Debes ingresar tu telefono", Toast.LENGTH_SHORT).show()
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Debes ingresar tu contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Debes confirmar tu contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show()
            return false
        }

        return true

    }

    private fun goToLogin() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}