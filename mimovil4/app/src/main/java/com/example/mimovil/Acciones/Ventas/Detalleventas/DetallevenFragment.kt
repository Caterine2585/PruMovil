package com.example.mimovil.Acciones.Ventas.Detalleventas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Ventas.DetalleVentas.Detallevenactualizar
import com.example.mimovil.Acciones.Ventas.DetalleVentas.Detalleveneliminar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetallevenFragment : Fragment() {

    private lateinit var tvResultadoDetalle: TextView
    private lateinit var btnOpcionesdetalle: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_detalleventa, container, false)

        tvResultadoDetalle = view.findViewById(R.id.tvResultadoDetalleVenta)
        btnOpcionesdetalle = view.findViewById(R.id.btnOpcionesDetalleVenta)

        mostrarDetalleVentas()

        btnOpcionesdetalle.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    // MENÚ BOTTOM
    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val menuView = layoutInflater.inflate(R.layout.opciondetalleventas, null)
        bottomSheet.setContentView(menuView)

        val opVer = menuView.findViewById<LinearLayout>(R.id.opverdetalleventas)
        val opRegistrar = menuView.findViewById<LinearLayout>(R.id.opregistrardetalleventas)
        val opActualizar = menuView.findViewById<LinearLayout>(R.id.opactualizardetalleventas)
        val opEliminar = menuView.findViewById<LinearLayout>(R.id.opEliminardetalleventas)


        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetallevenFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detallevenregistrar())
                .addToBackStack(null)
                .commit()
        }
        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detallevenactualizar())
                .addToBackStack(null)
                .commit()
        }



        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalleveneliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    // GET DETALLE VENTAS
    private fun mostrarDetalleVentas() {

        RetroFitInstance.api2kotlin.getDetalleVentas()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoDetalle.text = "No hay detalles de ventas registrados."
                        } else {

                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________") // mismo separador

                                if (partes.size >= 4) {
                                    """
                                        Cantidad: ${partes.getOrNull(0)}
                                        Fecha Salida: ${partes.getOrNull(1)}
                                        ID Producto: ${partes.getOrNull(2)}
                                        ID Venta: ${partes.getOrNull(3)}
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
