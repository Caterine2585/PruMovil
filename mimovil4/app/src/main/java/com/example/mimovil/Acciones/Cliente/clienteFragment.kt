package com.example.mimovil.Acciones.Cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class clienteFragment : Fragment() {

    private lateinit var tvResultadoClientes: TextView
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_cliente, container, false)

        tvResultadoClientes = view.findViewById(R.id.tvResultadoClientes)
        btnOpciones = view.findViewById(R.id.btnOpciones)

        mostrarClientes()

        btnOpciones.setOnClickListener {
            mostrarMenuOpciones()
        }

        return view
    }

    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opciones, null)
        bottomSheet.setContentView(view)

        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrar)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizar)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminar)

        // ✔ Navegar a Registrar cliente
        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteregistrar())
                .addToBackStack(null)
                .commit()
        }

        // ✔ Navegar a Actualizar cliente
        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteactualizar())
                .addToBackStack(null)
                .commit()
        }

        // ✔ Navegar a Eliminar cliente
        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteeliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    private fun mostrarClientes() {

        RetroFitInstance.api2kotlin.getClientes()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoClientes.text = "No hay clientes disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 7) {
                                    """
                                        Documento: ${partes[0]}
                                        Nombre: ${partes[1]}
                                        Apellido: ${partes[2]}
                                        Teléfono: ${partes[3]}
                                        Fecha Nacimiento: ${partes[4]}
                                        Género: ${partes[5]}
                                        Estado: ${partes[6]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoClientes.text = texto
                        }

                    } else {
                        tvResultadoClientes.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoClientes.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
