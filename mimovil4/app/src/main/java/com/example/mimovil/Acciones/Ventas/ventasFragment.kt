package com.example.mimovil.Acciones.Ventas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Ventas.Detalleventas.DetallevenFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ventasFragment : Fragment() {

    private lateinit var tvResultadoVentas: TextView
    private lateinit var btnOpcionesVentas: ImageButton
    private lateinit var btnDetalleVenta: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_venta, container, false)

        tvResultadoVentas = view.findViewById(R.id.tvResultadoVentas)
        btnOpcionesVentas = view.findViewById(R.id.btnOpcionesVenta)
        btnDetalleVenta = view.findViewById(R.id.btnDetalleventa)

        mostrarVentas()

        btnOpcionesVentas.setOnClickListener {
            mostrarMenuOpciones()
        }

        btnDetalleVenta.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetallevenFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }



    // MENÚ OPCIONES
    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionventa, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverventa)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarventa)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarventa)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarventa)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventaseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }



    // GET VENTAS
    private fun mostrarVentas() {

        RetroFitInstance.api2kotlin.getVentas()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoVentas.text = "No hay ventas registradas."
                        } else {

                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 3) {
                                    """
                                        ID Venta: ${partes[0]}
                                        Documento Cliente: ${partes[1]}
                                        Documento Empleado: ${partes[2]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoVentas.text = texto
                        }

                    } else {
                        tvResultadoVentas.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoVentas.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
