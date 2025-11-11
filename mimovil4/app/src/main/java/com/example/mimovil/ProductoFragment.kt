package com.example.mimovil


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Cliente
import com.example.mimovil.model.Producto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoFragment : Fragment() {

    private lateinit var etID_Producto: EditText
    private lateinit var etNombre_Producto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio_Venta: EditText
    private lateinit var etStock_Minimo: EditText
    private lateinit var etID_Categoria: EditText
    private lateinit var etID_Estado: EditText
    private lateinit var etID_Gama: EditText
    private lateinit var etFotos: EditText

    private lateinit var tvResultadoProducto: TextView
    private lateinit var btnCrearPro: Button
    private lateinit var btnMostrarPro: Button
    private lateinit var btnActualizarPro: Button
    private lateinit var btnEliminarPro: Button


        val etID_Producto   = view.findViewById<EditText>(R.id.etID_Producto)
        val etNombre_Producto     = view.findViewById<EditText>(R.id.etNombre_Producto)
        val etDescripcion      = view.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio_Venta    = view.findViewById<EditText>(R.id.etPrecio_Venta)
        val etStock_Minimo        = view.findViewById<EditText>(R.id.etStock_Minimo)
        val etID_Categoria      = view.findViewById<EditText>(R.id.etID_Categoria)
        val etID_Estado    = view.findViewById<EditText>(R.id.etID_Estado)
        val etID_Gama      = view.findViewById<EditText>(R.id.etID_Gama)
        val etFotos      = view.findViewById<EditText>(R.id.etFotos)

        val btnCrear      = view.findViewById<Button>(R.id.btnCrearProducto)
        val btnActualizarProducto = view.findViewById<Button>(R.id.btnActualizarProducto)


        btnCrear.setOnClickListener {
            val producto = Producto(
                ID_Producto     = etID_Producto.text.toString().trim(),
                Nombre_Producto = etNombre_Producto.text.toString().trim(),
                Descripcion        = etDescripcion.text.toString().trim(),
                Precio_Venta      = etPrecio_Venta.text.toString().trim(),
                Stock_Minimo          = etStock_Minimo.text.toString().trim(),
                ID_Categoria        = etID_Categoria.text.toString().trim(),
                ID_Estado      = etID_Estado.text.toString().trim().ifEmpty { "EST001" },
                ID_Gama        = etID_Gama.text.toString().trim(),
                Fotos      = etFotos.text.toString().trim(),
                )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_producto, container, false)

        // 🔹 Inicializar campos
        etID_Producto = view.findViewById(R.id.etID_Producto)
        etNombre_Producto = view.findViewById(R.id.etNombre_Producto)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etPrecio_Venta = view.findViewById(R.id.etPrecio_Venta)
        etStock_Minimo = view.findViewById(R.id.etStock_Minimo)
        etID_Categoria = view.findViewById(R.id.etID_Categoria)
        etID_Estado = view.findViewById(R.id.etID_Estado)
        etID_Gama = view.findViewById(R.id.etID_Gama)
        etFotos = view.findViewById(R.id.etFotos)

        btnCrearPro = view.findViewById(R.id.btnCrearProducto)
        btnMostrarPro = view.findViewById(R.id.btnMostrarProductos)
        btnActualizarPro = view.findViewById(R.id.btnActualizarProducto)
        btnEliminarPro = view.findViewById(R.id.btnEliminarProducto)
        tvResultadoProducto = view.findViewById(R.id.tvResultadoProducto)

        // 🔹 Eventos
        btnCrearPro.setOnClickListener { crearProducto() }
        btnMostrarPro.setOnClickListener { mostrarProducto() }
        btnActualizarPro.setOnClickListener { actualizarProducto() }
        btnEliminarPro.setOnClickListener { eliminarProducto() }
        
            RetroFitInstance.api2kotlin.crearProducto(producto)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val body = response.body()?.string().orEmpty()
                            Toast.makeText(requireContext(), "OK: $body", Toast.LENGTH_LONG).show()
                            limpiarCampos(etID_Producto, etNombre_Producto, etDescripcion, etPrecio_Venta, etStock_Minimo, etID_Categoria, etID_Estado, etID_Gama, etFotos)
                        } else {
                            val err = response.errorBody()?.string().orEmpty()
                            Toast.makeText(requireContext(), "Error: ${response.code()} $err", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
        fun btnActualizarProducto() {
            val idproducto = etID_Producto.text.toString().trim()
            if (idproducto.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa el ID del Producto para actualizar", Toast.LENGTH_SHORT).show()
                return
            }

            val producto = Producto(
                ID_Producto = idproducto,
                Nombre_Producto = etNombre_Producto.text.toString(),
                Descripcion = etDescripcion.text.toString(),
                Precio_Venta = etPrecio_Venta.text.toString(),
                Stock_Minimo = etStock_Minimo.text.toString(),
                ID_Categoria = etID_Categoria.text.toString(),
                ID_Estado = etID_Estado.text.toString(),
                ID_Gama = etID_Gama.text.toString(),
                Fotos = etFotos.text.toString()

            )

            RetroFitInstance.api2kotlin.ActualizarProducto(idproducto, producto)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_LONG).show()
                            limpiarCampos()
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
        return view
    }

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
            Fotos = etFotos.text.toString().trim(),

            )

        if (producto.ID_Producto.isEmpty() || producto.Nombre_Producto.isEmpty()) {
            Toast.makeText(requireContext(), "ID del Producto y Nombre del Producto son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearProducto(producto)
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
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
    private fun mostrarProducto() {
        RetroFitInstance.api2kotlin.getProducto()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoProducto.text = "No hay Productos disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 7) {
                                    """
                                    ID Producto: ${partes[0]}
                                    Nombre_Producto: ${partes[1]} ${partes[2]}
                                    Descripcion: ${partes[3]}
                                    Precio_Venta: ${partes[4]}
                                    Stock_Minimo: ${partes[5]}
                                    ID_Categoria: ${partes[6]}
                                    ID_Estado: ${partes[7]}
                                    ID_Gama: ${partes[8]}
                                    Fotos: ${partes[9]}
                                    """.trimIndent()
                                } else {
                                    "⚠️ Formato incorrecto: $item"
                                }
                            }
                            tvResultadoProducto.text = texto
                        }
                    } else {
                        tvResultadoProducto.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoProducto.text = "Error de conexión: ${t.message}"
                }
            })
    }

    private fun actualizarProducto() {
        val id_producto = etID_Producto.text.toString().trim()
        if (id_producto.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el documento para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = Producto(
            ID_Producto = id_producto,
            Nombre_Producto = etNombre_Producto.text.toString(),
            Descripcion = etDescripcion.text.toString(),
            Precio_Venta = etPrecio_Venta.text.toString(),
            Stock_Minimo = etStock_Minimo.text.toString(),
            ID_Categoria = etID_Categoria.text.toString(),
            ID_Estado = etID_Estado.text.toString(),
            ID_Gama = etID_Gama.text.toString(),
            Fotos = etFotos.text.toString()

        )

        RetroFitInstance.api2kotlin.ActualizarProducto(id_producto, producto)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun eliminarProducto() {
        val id_producto = etID_Producto.text.toString().trim()
        if (id_producto.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID del Producto para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarProducto(id_producto)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto eliminado correctamente", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

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
}