package com.example.mimovil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.LoginRequest
import com.example.mimovil.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
        val tokenGuardado = prefs.getString("jwt_token", null)
        val rolGuardado = prefs.getString("rol", null)

        if (!tokenGuardado.isNullOrEmpty() && !rolGuardado.isNullOrEmpty()) {
            irSegunRol(rolGuardado)
            return
        }

        setContentView(R.layout.fragment_login)

        val etDocumento = findViewById<EditText>(R.id.etDocumentoLogin)
        val etContrasena = findViewById<EditText>(R.id.etContrasenaLogin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val documento = etDocumento.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (documento.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Documento y contraseña obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(
                documentoEmpleado = documento,
                contrasena = contrasena
            )


            RetroFitInstance.api2kotlin.loginEmpleado(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {

                            val body = response.body()

                            val token = body?.token.orEmpty()
                            val rol = body?.rol.orEmpty()

                            Log.d("TOKEN_DEBUG", "TOKEN RECIBIDO: $token")
                            Log.d("ROL_DEBUG", "ROL RECIBIDO: $rol")

                            if (token.length < 20 || rol.isEmpty()) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Token o rol inválido/no recibido",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }

                            prefs.edit()
                                .putString("jwt_token", token)
                                .putString("rol", rol)
                                .apply()

                            Toast.makeText(this@LoginActivity, "Login correcto", Toast.LENGTH_SHORT).show()
                            irSegunRol(rol)

                        } else {
                            val err = response.errorBody()?.string().orEmpty()
                            Toast.makeText(
                                this@LoginActivity,
                                "Error: ${response.code()} $err",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Fallo: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun irSegunRol(rol: String) {

        if (rol == "ROL001") {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, MainEmpleadoActivity::class.java))
        }
        finish()
    }
}
