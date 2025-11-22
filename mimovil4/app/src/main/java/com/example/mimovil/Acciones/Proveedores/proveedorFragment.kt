package com.example.mimovil.Acciones.Proveedor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Proveedores.Proveedorregistrar
import com.example.mimovil.Acciones.Proveedores.proveedoractualizar
import com.example.mimovil.Acciones.Proveedores.proveedoreliminar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class proveedorFragment : Fragment() {

    private lateinit var tvResultadoProveedores: TextView
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_proveedor, container, false)

        tvResultadoProveedores = view.findViewById(R.id.tvResultadoProveedor)
        btnOpciones = view.findViewById(R.id.btnOpcionesProveedor) // Asegúrate que exista en tu layout

        mostrarProveedores()

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

        val view = layoutInflater.inflate(R.layout.opcionproveedor, null)
        bottomSheet.setContentView(view)

        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarproveedor)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarproveedor)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarproveedor)

        // ✔ Navegar a Registrar proveedor
        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Proveedorregistrar())
                .addToBackStack(null)
                .commit()
        }

        // ✔ Navegar a Actualizar proveedor
        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, proveedoractualizar())
                .addToBackStack(null)
                .commit()
        }

        // ✔ Navegar a Eliminar proveedor
        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, proveedoreliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    private fun mostrarProveedores() {

        RetroFitInstance.api2kotlin.getProveedores()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoProveedores.text = "No hay proveedores disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 5) {
                                    """
                                        ID Proveedor: ${partes[0]}
                                        Nombre: ${partes[1]}
                                        Correo: ${partes[2]}
                                        Teléfono: ${partes[3]}
                                        Estado: ${partes[4]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoProveedores.text = texto
                        }

                    } else {
                        tvResultadoProveedores.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoProveedores.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
