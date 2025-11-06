package com.example.mimovil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Cliente

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val etDocumento = view.findViewById<EditText>(R.id.etDocumento)
        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etApellido = view.findViewById<EditText>(R.id.etApellido)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefono)
        val etFecha = view.findViewById<EditText>(R.id.etFecha)
        val etGenero = view.findViewById<EditText>(R.id.etGenero)
        val etEstado = view.findViewById<EditText>(R.id.etEstado)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val persona = com.example.mimovil.model.Cliente(
                documento = etDocumento.text.toString().trim(),
                nombre = etNombre.text.toString().trim(),
                apellido = etApellido.text.toString().trim(),
                telefono = etTelefono.text.toString().trim(),
                fecha = etFecha.text.toString().trim(),   // "YYYY-MM-DD"
                genero = etGenero.text.toString().trim(),
                estado = etEstado.text.toString().trim()
            )

            if (persona.documento.isEmpty() || persona.nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Faltan campos obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                enviarPost(persona)
            }
        }

        return view
    }

    private fun enviarPost(persona: Cliente) {
        // 🔹 ahora coincide con el tipo ResponseBody
        RetroFitInstance.api2kotlin.crearCliente(persona)
            .enqueue(object : retrofit2.Callback<okhttp3.ResponseBody> {
                override fun onResponse(
                    call: retrofit2.Call<okhttp3.ResponseBody>,
                    response: retrofit2.Response<okhttp3.ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val respuesta = response.body()?.string().orEmpty()
                        Toast.makeText(
                            requireContext(),
                            "POST OK: $respuesta",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val err = response.errorBody()?.string().orEmpty()
                        Toast.makeText(
                            requireContext(),
                            "POST Error: ${response.code()} $err",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<okhttp3.ResponseBody>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        requireContext(),
                        "POST Fail: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
