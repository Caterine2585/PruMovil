package com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
 import com.example.mimovil.Acciones.Devoluciones.DetalleDev.Detalledeveliminar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalledevFragment : Fragment() {

    private lateinit var tvResultadoDetalleDev: TextView
    private lateinit var btnOpcionesDetalleDev: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_detalledevolucion, container, false)

        tvResultadoDetalleDev = view.findViewById(R.id.tvResultadoDetalleDevolucion)
        btnOpcionesDetalleDev = view.findViewById(R.id.btnOpcionesDetalleDevolucion)

        mostrarDetalleDevolucion()

        btnOpcionesDetalleDev.setOnClickListener { mostrarMenuOpciones() }

        return view
    }



    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val menuView = layoutInflater.inflate(R.layout.opciondetalledevolucion, null)
        bottomSheet.setContentView(menuView)

        val opVer = menuView.findViewById<LinearLayout>(R.id.opverdetalledevolucion)
        val opRegistrar = menuView.findViewById<LinearLayout>(R.id.opregistrardetalledevolucion)
        val opActualizar = menuView.findViewById<LinearLayout>(R.id.opactualizardetalledevolucion)
        val opEliminar = menuView.findViewById<LinearLayout>(R.id.opEliminardetalledevolucion)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetalledevFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledevregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledevactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledeveliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }



    private fun mostrarDetalleDevolucion() {

        RetroFitInstance.api2kotlin.getDetalleDev()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoDetalleDev.text = "No hay detalles de devolución registrados."
                        } else {

                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 4) {
                                    """
                                        ID DetalleDev: ${partes.getOrNull(0)}
                                        ID Devolución: ${partes.getOrNull(1)}
                                        ID Venta: ${partes.getOrNull(2)}
                                        Cantidad Devuelta: ${partes.getOrNull(3)}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoDetalleDev.text = texto
                        }

                    } else {
                        tvResultadoDetalleDev.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoDetalleDev.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
