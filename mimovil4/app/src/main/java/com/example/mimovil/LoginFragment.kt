package com.example.mimovil


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.ContraseñaDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay token guardado, pasar directo al MainActivity
        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (!token.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.fragment_login)

        val documento = findViewById<EditText>(R.id.editDocumento)
        val contrasena = findViewById<EditText>(R.id.editContrasena)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val documento = documento.text.toString()
            val contrasena = contrasena.text.toString()

            if (documento.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LOGIN", "Enviando usuario: $documento, contraseña: $contrasena")
            val request = mapOf(
                "usuario" to documento,
                "contrasena" to contrasena
            )

            // Llamada al backend con Retrofit
            RetroFitInstance.api2kotlin.login(request)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                            val token = response.body()
                            Log.d("LOGIN", "Token recibido: $token")

                            Log.d("LOGIN", "Código de respuesta: ${response.code()}")
                            Log.d("LOGIN", "Cuerpo de respuesta: ${response.errorBody()?.string()}")


                            // 🔹 Guardar token JWT solo si existe
                            prefs.edit().putString("jwt_token", token).apply()

                            Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
