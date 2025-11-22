package com.example.mimovil.Acciones.Empleado

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Empleado
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class empleadoFragment : Fragment() {

    private lateinit var tvEmpleados: TextView
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_empleado, container, false)


        tvEmpleados = view.findViewById(R.id.tvResultadoEmpleado)
        btnOpciones = view.findViewById(R.id.btnOpcionesempleado)

        mostrarEmpleados()

        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    private fun mostrarEmpleados() {
        RetroFitInstance.api2kotlin.getEmpleados().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val data = response.body().orEmpty()
                    if (data.isEmpty()) {
                        tvEmpleados.text = "No hay empleados registrados."
                    } else {
                        val texto = data.joinToString("\n\n") { item ->
                            val partes = item.split("________")
                            if (partes.size >= 9) {
                                """
                        Nombre: ${partes[2]} 
                        Apellido:${partes[3]}
                        Documento: ${partes[1]}
                        Tipo de documento: ${partes[0]}
                        Edad: ${partes[4]}
                        Correo: ${partes[5]}
                        Teléfono: ${partes[6]}
                        Género: ${partes[7]}
                        Estado: ${partes[8]}
                        Rol: ${partes[9]}
                        Fotos :${partes[10]}
                        """.trimIndent()
                            } else {
                                "Formato incorrecto: $item"
                            }
                        }
                        tvEmpleados.text = texto
                    }
                } else {
                    tvEmpleados.text = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                tvEmpleados.text = "Error de conexión: ${t.message}"
            }
        })
    }

        private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)

        view.findViewById<android.widget.LinearLayout>(R.id.opregistraremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoregistrar())
                .addToBackStack(null).commit()
        }

        view.findViewById<android.widget.LinearLayout>(R.id.opactualizaremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoactualizar())
                .addToBackStack(null).commit()
        }

        view.findViewById<android.widget.LinearLayout>(R.id.opEliminaremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoeliminar())
                .addToBackStack(null).commit()
        }

        bottomSheet.show()
    }
}
