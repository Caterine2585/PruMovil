package com.example.mimovil.Acciones.Producto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoFragment : Fragment() {

    private lateinit var tvResultadoProductos: TextView
    private lateinit var btnOpcionesProductos: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_producto, container, false)

        tvResultadoProductos = view.findViewById(R.id.tvResultadoProducto)
        btnOpcionesProductos = view.findViewById(R.id.btnOpcionesProducto)

        mostrarProductos()

        btnOpcionesProductos.setOnClickListener {
            mostrarMenuOpciones()
        }

        return view
    }

    // ==================== MENÚ DESPLEGABLE ====================
    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )
        val view = layoutInflater.inflate(R.layout.opcionproductos, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverProductos)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarProductos)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarProductos)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarProductos)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductoFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductosRegistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductoActualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductoEliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    // ==================== GET: MOSTRAR PRODUCTOS ====================
    private fun mostrarProductos() {

        val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            tvResultadoProductos.text = "Debes iniciar sesión primero."
            return
        }

        val authHeader = "Bearer $token"

        RetroFitInstance.api2kotlin.getProducto(authHeader)
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoProductos.text = "No hay productos disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->

                                val partes = item.split("________")

                                if (partes.size >= 9) {
                                    """
                                    ID_Producto: ${partes[0]}
                                    Nombre: ${partes[1]}
                                    Descripción: ${partes[2]}
                                    Precio Venta: ${partes[3]}
                                    Stock Mínimo: ${partes[4]}
                                    ID_Categoría: ${partes[5]}
                                    ID_Estado: ${partes[6]}
                                    ID_Gama: ${partes[7]}
                                    Foto: ${partes[8]}
                                """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }

                            tvResultadoProductos.text = texto
                        }

                    } else {
                        tvResultadoProductos.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoProductos.text = "Error de conexión: ${t.message}"
                }
            })
    }
}