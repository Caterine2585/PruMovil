package com.example.mimovil.Acciones.Compras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Compras.Detallecompras.detallecomprasFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComprasFragment : Fragment() {

    private lateinit var tvResultadoCompras: TextView
    private lateinit var btnOpcionesCompras: ImageButton
    private lateinit var btnDetalleCompra: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_compra, container, false)

        tvResultadoCompras = view.findViewById(R.id.tvResultadoCompras)
        btnOpcionesCompras = view.findViewById(R.id.btnOpcionesCompras)
        btnDetalleCompra = view.findViewById(R.id.btnDetalleCompra)

        mostrarCompras()

        btnOpcionesCompras.setOnClickListener {
            mostrarMenuOpciones()
        }


        btnDetalleCompra.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasFragment())
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

        val view = layoutInflater.inflate(R.layout.opcionescompras, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverCompra)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarCompra)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarCompra)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarCompra)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ComprasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Comprasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Comprasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Compraseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }



    //GET COMPRAS

    private fun mostrarCompras() {

        RetroFitInstance.api2kotlin.getCompras()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoCompras.text = "No hay compras registradas."
                        } else {

                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 4) {
                                    """
                                        ID Entrada: ${partes[0]}
                                        Precio Compra: ${partes[1]}
                                        ID Producto: ${partes[2]}
                                        Documento Empleado: ${partes[3]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoCompras.text = texto
                        }

                    } else {
                        tvResultadoCompras.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoCompras.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
