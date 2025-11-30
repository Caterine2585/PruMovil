package com.example.mimovil

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Producto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoFragment : Fragment(R.layout.fragment_producto) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etID_Producto     = view.findViewById<EditText>(R.id.etID_Producto)
        val etNombre_Producto = view.findViewById<EditText>(R.id.etNombre_Producto)
        val etDescripcion     = view.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio_Venta    = view.findViewById<EditText>(R.id.etPrecio_Venta)
        val etStock_Minimo    = view.findViewById<EditText>(R.id.etStock_Minimo)
        val etID_Categoria    = view.findViewById<EditText>(R.id.etID_Categoria)
        val etID_Estado       = view.findViewById<EditText>(R.id.etID_Estado)
        val etID_Gama         = view.findViewById<EditText>(R.id.etID_Gama)
        val etFotos           = view.findViewById<EditText>(R.id.etFotos)
        val btnCrear          = view.findViewById<Button>(R.id.btnCrearProducto)
        val btnGet            = view.findViewById<Button>(R.id.btnGetProductos)
        val tvProductos       = view.findViewById<TextView>(R.id.tvProductos)

        // ========== POST: Crear producto ==========
        btnCrear.setOnClickListener {
            val producto = Producto(
                ID_Producto     = etID_Producto.text.toString().trim(),
                Nombre_Producto = etNombre_Producto.text.toString().trim(),
                Descripcion     = etDescripcion.text.toString().trim(),
                Precio_Venta    = etPrecio_Venta.text.toString().trim(),
                Stock_Minimo    = etStock_Minimo.text.toString().trim(),
                ID_Categoria    = etID_Categoria.text.toString().trim(),
                ID_Estado       = etID_Estado.text.toString().trim().ifEmpty { "EST001" },
                ID_Gama         = etID_Gama.text.toString().trim(),
                Fotos           = etFotos.text.toString().trim()
            )

            if (producto.ID_Producto.isEmpty() || producto.Nombre_Producto.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "ID Producto y Nombre del Producto son obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt_token", null)

            if (token.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Debes iniciar sesión primero (token no encontrado)",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val authHeader = "Bearer $token"

            RetroFitInstance.api2kotlin.crearProducto(authHeader, producto)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()?.string().orEmpty()
                            Toast.makeText(requireContext(), "OK: $body", Toast.LENGTH_LONG).show()
                            limpiarCampos(
                                etID_Producto,
                                etNombre_Producto,
                                etDescripcion,
                                etPrecio_Venta,
                                etStock_Minimo,
                                etID_Categoria,
                                etID_Estado,
                                etID_Gama,
                                etFotos
                            )
                        } else {
                            val err = response.errorBody()?.string().orEmpty()
                            Toast.makeText(
                                requireContext(),
                                "Error: ${response.code()} $err",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Fallo: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        // ========== GET: Listar productos ==========
        btnGet.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt_token", null)

            if (token.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Debes iniciar sesión primero (token no encontrado)",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
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
                            if (data.isNotEmpty()) {
                                val texto = data.joinToString("\n\n") { it }
                                tvProductos.text = texto
                            } else {
                                tvProductos.text = "No hay productos disponibles"
                            }
                        } else {
                            val err = response.errorBody()?.string().orEmpty()
                            Toast.makeText(
                                requireContext(),
                                "Error: ${response.code()} $err",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<String>>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Fallo: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun limpiarCampos(vararg ets: EditText) {
        ets.forEach { it.text?.clear() }
    }
}
