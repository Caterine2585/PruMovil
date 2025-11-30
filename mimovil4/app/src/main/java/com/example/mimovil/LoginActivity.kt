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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 👉 Si ya hay token guardado, salta el login
        val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
        val tokenGuardado = prefs.getString("jwt_token", null)
        if (tokenGuardado != null) {
            irAMain()
            return
        }

        setContentView(R.layout.fragment_login)

        val etDocumento   = findViewById<EditText>(R.id.etDocumentoLogin)
        val etContrasena  = findViewById<EditText>(R.id.etContrasenaLogin)
        val btnLogin      = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val documento  = etDocumento.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (documento.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Documento y contraseña obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ AHORA COINCIDE CON EL BACKEND:
            //  documento_Empleado  +  contrasena
            val request = LoginRequest(
                documentoEmpleado = documento,
                contrasena = contrasena
            )

            RetroFitInstance.api2kotlin.loginEmpleado(request)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {

                            val token = response.body()?.string().orEmpty()

                            // 🔥 Ver el token en Logcat
                            Log.d("TOKEN_DEBUG", "TOKEN RECIBIDO: $token")

                            if (token.length < 20) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Token inválido o no recibido",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }

                            // 👉 Guardar token
                            val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
                            prefs.edit()
                                .putString("jwt_token", token)
                                .apply()

                            Toast.makeText(this@LoginActivity, "Login correcto", Toast.LENGTH_SHORT).show()
                            irAMain()

                        } else {
                            val err = response.errorBody()?.string().orEmpty()
                            Toast.makeText(
                                this@LoginActivity,
                                "Error: ${response.code()} $err",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Fallo: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun irAMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
