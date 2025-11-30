package com.example.mimovil.Acciones.Compras.Detallecompras

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

class detallecomprasFragment : Fragment() {

    private lateinit var tvResultadoDetalle: TextView
    private lateinit var btnOpcionesdetalle: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_detallecompras, container, false)

        tvResultadoDetalle = view.findViewById(R.id.tvResultadoDetalleCompra)
        btnOpcionesdetalle = view.findViewById(R.id.btnOpcionesDetalleCompra)

        mostrarDetalleCompras()

        // 🔥 Aquí va el menú de opciones
        btnOpcionesdetalle.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


            //MENÚ BOTTOM

    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val menuView = layoutInflater.inflate(R.layout.opcionesdetallecompras, null)
        bottomSheet.setContentView(menuView)

        val opVer = menuView.findViewById<LinearLayout>(R.id.opverdetallecompras)
        val opRegistrar = menuView.findViewById<LinearLayout>(R.id.opregistrardetallecompras)
        val opActualizar = menuView.findViewById<LinearLayout>(R.id.opactualizardetallecompras)
        val opEliminar = menuView.findViewById<LinearLayout>(R.id.opEliminardetallecompras)

        // VER
        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasFragment())
                .addToBackStack(null)
                .commit()
        }

        // REGISTRAR
        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasregistrar())
                .addToBackStack(null)
                .commit()
        }

        // ACTUALIZAR
        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasactualizar())
                .addToBackStack(null)
                .commit()
        }

        // ELIMINAR
        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecompraseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }



    // GET DETALLE COMPRAS

    private fun mostrarDetalleCompras() {

        RetroFitInstance.api2kotlin.getDetalleCompras()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoDetalle.text = "No hay detalles de compras registrados."
                        } else {

                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________") // SEPARADOR DE TU API

                                if (partes.size >= 4) {
                                    """
                                       ID Entrada: ${partes.getOrNull(0)}
                                        ID Proveedor: ${partes.getOrNull(1)}
                                        Cantidad: ${partes.getOrNull(2)}
                                        Fecha_Entrada: ${partes.getOrNull(3)}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoDetalle.text = texto
                        }

                    } else {
                        tvResultadoDetalle.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoDetalle.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
