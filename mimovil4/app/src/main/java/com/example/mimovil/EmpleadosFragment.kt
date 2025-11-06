package com.example.mimovil.model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpleadosFragment : Fragment(R.layout.fragment_empleados) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etDocumento   = view.findViewById<EditText>(R.id.etDocumentoEmp)
        val etTipoDoc     = view.findViewById<EditText>(R.id.etTipoDocEmp)
        val etNombre      = view.findViewById<EditText>(R.id.etNombreEmp)
        val etApellido    = view.findViewById<EditText>(R.id.etApellidoEmp)
        val etEdad        = view.findViewById<EditText>(R.id.etEdadEmp)
        val etCorreo      = view.findViewById<EditText>(R.id.etCorreoEmp)
        val etTelefono    = view.findViewById<EditText>(R.id.etTelefonoEmp)
        val etGenero      = view.findViewById<EditText>(R.id.etGeneroEmp)
        val etEstado      = view.findViewById<EditText>(R.id.etEstadoEmp)
        val etRol         = view.findViewById<EditText>(R.id.etRolEmp)
        val etFotos       = view.findViewById<EditText>(R.id.etFotosEmp)
        val btnCrear      = view.findViewById<Button>(R.id.btnCrearEmpleado)

        btnCrear.setOnClickListener {
            val emp = Empleado(
                documento     = etDocumento.text.toString().trim(),
                tipoDocumento = etTipoDoc.text.toString().trim(),
                nombre        = etNombre.text.toString().trim(),
                apellido      = etApellido.text.toString().trim(),
                edad          = etEdad.text.toString().trim(),
                correo        = etCorreo.text.toString().trim(),
                telefono      = etTelefono.text.toString().trim(),
                genero        = etGenero.text.toString().trim(),
                idEstado      = etEstado.text.toString().trim().ifEmpty { "EST001" },
                idRol         = etRol.text.toString().trim().ifEmpty { "ROL002" },
                fotos         = etFotos.text.toString().trim()
            )

            if (emp.documento.isEmpty() || emp.nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Documento y Nombre son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetroFitInstance.api2kotlin.crearEmpleado(emp)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val body = response.body()?.string().orEmpty()
                            Toast.makeText(requireContext(), "OK: $body", Toast.LENGTH_LONG).show()
                            limpiarCampos(etDocumento, etTipoDoc, etNombre, etApellido, etEdad, etCorreo, etTelefono, etGenero, etEstado, etRol, etFotos)
                        } else {
                            val err = response.errorBody()?.string().orEmpty()
                            Toast.makeText(requireContext(), "Error: ${response.code()} $err", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    private fun limpiarCampos(vararg ets: EditText) {
        ets.forEach { it.text?.clear() }
    }
}
