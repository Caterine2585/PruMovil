package com.example.mimovil.Acciones.Devoluciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion.DetalledevFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DevolucionFragment : Fragment() {

    private lateinit var tvResultadoDevoluciones: TextView
    private lateinit var btnOpcionesDevoluciones: ImageButton
    private lateinit var btnDetalleDevolucion: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_devolucion, container, false)

        tvResultadoDevoluciones = view.findViewById(R.id.tvResultadoDevolucion)
        btnOpcionesDevoluciones = view.findViewById(R.id.btnOpcionesDevolucion)
        btnDetalleDevolucion = view.findViewById(R.id.btnDetalleDevoluciones)

        mostrarDevoluciones()

        btnOpcionesDevoluciones.setOnClickListener {
            mostrarMenuOpciones()
        }

        btnDetalleDevolucion.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetalledevFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }



    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opciondevolucion, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdevolucion)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardevolucion)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardevolucion)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardevolucion)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DevolucionFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucionregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucionactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucioneliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }



    private fun mostrarDevoluciones() {

        RetroFitInstance.api2kotlin.getDevolucion()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoDevoluciones.text = "No hay devoluciones registradas."
                        } else {

                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 3) {
                                    """
                                        ID Devolución: ${partes[0]}
                                        Fecha: ${partes[1]}
                                        Motivo: ${partes[2]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoDevoluciones.text = texto
                        }

                    } else {
                        tvResultadoDevoluciones.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoDevoluciones.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
