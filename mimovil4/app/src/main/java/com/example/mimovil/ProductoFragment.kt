package com.example.mimovil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Producto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoFragment : Fragment() {

    // 🔹 Campos
    private lateinit var etID_Producto: EditText
    private lateinit var etNombre_Producto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio_Venta: EditText
    private lateinit var etStock_Minimo: EditText
    private lateinit var etID_Categoria: EditText
    private lateinit var etID_Estado: EditText
    private lateinit var etID_Gama: EditText
    private lateinit var etFotos: EditText

    private lateinit var btnCrear: Button
    private lateinit var btnMostrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button

    private lateinit var tvResultadoProductos: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_producto, container, false)

        // Inicializar campos
        etID_Producto = view.findViewById(R.id.etID_Producto)
        etNombre_Producto = view.findViewById(R.id.etNombre_Producto)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etPrecio_Venta = view.findViewById(R.id.etPrecio_Venta)
        etStock_Minimo = view.findViewById(R.id.etStock_Minimo)
        etID_Categoria = view.findViewById(R.id.etID_Categoria)
        etID_Estado = view.findViewById(R.id.etID_Estado)
        etID_Gama = view.findViewById(R.id.etID_Gama)
        etFotos = view.findViewById(R.id.etFotos)

        btnCrear = view.findViewById(R.id.btnCrearProducto)
        btnMostrar = view.findViewById(R.id.btnMostrarProductos)
        btnActualizar = view.findViewById(R.id.btnActualizarProducto)
        btnEliminar = view.findViewById(R.id.btnEliminarProducto)
        tvResultadoProductos = view.findViewById(R.id.tvResultadoProducto)

        // Eventos
        btnCrear.setOnClickListener { crearProducto() }
        btnMostrar.setOnClickListener { mostrarProductos() }
        btnActualizar.setOnClickListener { actualizarProducto() }
        btnEliminar.setOnClickListener { eliminarProducto() }

        return view
    }

    // 🔐 Obtener token Bearer
    private fun getToken(): String? {
        val prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        return token?.let { "Bearer $it" }
    }

    // ======================================
    // ✅ POST: Crear producto
    // ======================================
    private fun crearProducto() {
        val producto = Producto(
            ID_Producto = etID_Producto.text.toString().trim(),
            Nombre_Producto = etNombre_Producto.text.toString().trim(),
            Descripcion = etDescripcion.text.toString().trim(),
            Precio_Venta = etPrecio_Venta.text.toString().trim(),
            Stock_Minimo = etStock_Minimo.text.toString().trim(),
            ID_Categoria = etID_Categoria.text.toString().trim(),
            ID_Estado = etID_Estado.text.toString().trim().ifEmpty { "EST001" },
            ID_Gama = etID_Gama.text.toString().trim(),
            Fotos = etFotos.text.toString().trim()
        )

        if (producto.ID_Producto.isEmpty() || producto.Nombre_Producto.isEmpty()) {
            Toast.makeText(requireContext(), "ID y Nombre del producto son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val bearer = getToken() ?: return showTokenError()

        RetroFitInstance.api2kotlin.crearProducto(bearer, producto)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto creado correctamente", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ======================================
    // ✅ GET: Mostrar productos
    // ======================================
    private fun mostrarProductos() {
        val token = getToken() ?: return showTokenError()
            RetroFitInstance.api2kotlin.getProductos(token)
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        tvResultadoProductos.text = "Error: ${response.code()}"
                        return
                    }

                    val data = response.body().orEmpty()
                    if (data.isEmpty()) {
                        tvResultadoProductos.text = "No hay productos disponibles."
                        return
                    }

                    val texto = data.joinToString("\n\n") { item ->
                        val p = item.split("________")
                        if (p.size >= 9) """
                            ID Producto: ${p[0]}
                            Nombre: ${p[1]}
                            Descripción: ${p[2]}
                            Precio Venta: ${p[3]}
                            Stock Mínimo: ${p[4]}
                            ID Categoría: ${p[5]}
                            ID Estado: ${p[6]}
                            ID Gama: ${p[7]}
                            Fotos: ${p[8]}
                        """.trimIndent()
                        else "⚠ Formato incorrecto: $item"
                    }

                    tvResultadoProductos.text = texto
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoProductos.text = "Error de conexión: ${t.message}"
                }
            })
    }

    // ======================================
    // ✅ PUT: Actualizar producto
    // ======================================
    private fun actualizarProducto() {
        val id = etID_Producto.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID del producto", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = Producto(
            ID_Producto = id,
            Nombre_Producto = etNombre_Producto.text.toString(),
            Descripcion = etDescripcion.text.toString(),
            Precio_Venta = etPrecio_Venta.text.toString(),
            Stock_Minimo = etStock_Minimo.text.toString(),
            ID_Categoria = etID_Categoria.text.toString(),
            ID_Estado = etID_Estado.text.toString(),
            ID_Gama = etID_Gama.text.toString(),
            Fotos = etFotos.text.toString()
        )

        val bearer = getToken() ?: return showTokenError()

        RetroFitInstance.api2kotlin.actualizarProducto(bearer, id, producto)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ======================================
    // ✅ DELETE: Eliminar producto
    // ======================================
    private fun eliminarProducto() {
        val id = etID_Producto.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID del producto", Toast.LENGTH_SHORT).show()
            return
        }

        val bearer = getToken() ?: return showTokenError()

        RetroFitInstance.api2kotlin.eliminarProducto(bearer, id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ======================================
    // 🔄 Limpiar campos
    // ======================================
    private fun limpiarCampos() {
        etID_Producto.text.clear()
        etNombre_Producto.text.clear()
        etDescripcion.text.clear()
        etPrecio_Venta.text.clear()
        etStock_Minimo.text.clear()
        etID_Categoria.text.clear()
        etID_Estado.text.clear()
        etID_Gama.text.clear()
        etFotos.text.clear()
    }

    private fun showTokenError() {
        Toast.makeText(requireContext(), "Token no disponible. Inicia sesión.", Toast.LENGTH_SHORT).show()
    }
}
