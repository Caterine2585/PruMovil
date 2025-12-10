package com.example.mimovil.Acciones.Producto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Producto
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoActualizar : Fragment() {

    private lateinit var etBuscarID: EditText

    private lateinit var etID_Producto: EditText
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStockMin: EditText
    private lateinit var etCategoria: EditText
    private lateinit var etEstado: EditText
    private lateinit var etGama: EditText
    private lateinit var etFoto: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_producto, container, false)

        etBuscarID    = view.findViewById(R.id.etBuscarID_Producto)


        etID_Producto = view.findViewById(R.id.etID_Producto)
        etNombre      = view.findViewById(R.id.etNombre_Producto)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etPrecio      = view.findViewById(R.id.etPrecio_Venta)
        etStockMin    = view.findViewById(R.id.etStock_Minimo)
        etCategoria   = view.findViewById(R.id.etID_Categoria)
        etEstado      = view.findViewById(R.id.etID_Estado)
        etGama        = view.findViewById(R.id.etID_Gama)
        etFoto        = view.findViewById(R.id.etFotos)

        btnBuscar     = view.findViewById(R.id.btnBuscarProducto)
        btnActualizar = view.findViewById(R.id.btnActualizarProducto)
        btnOpciones   = view.findViewById(R.id.btnOpcionesProducto)

        btnBuscar.setOnClickListener { buscarProducto() }
        btnActualizar.setOnClickListener { actualizarProducto() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    // ============================================================
    // MENÚ DESPLEGABLE
    // ============================================================

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


    // ============================================================
    // GET: BUSCAR PRODUCTO
    // ============================================================

    private fun buscarProducto() {
        val id = etBuscarID.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener token (mejor usar auth real)
        val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Debes iniciar sesión primero", Toast.LENGTH_SHORT).show()
            return
        }
        val authHeader = "Bearer $token"

        RetroFitInstance.api2kotlin.getProducto(authHeader)
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al obtener productos: ${response.code()}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    // DEBUG: si quieres ver la lista en Logcat o en pantalla temporalmente
                    // Log.d("BUSCAR_PROD", "Lista size=${lista.size} -> $lista")
                    // tvResultadoProductos.text = lista.joinToString("\n") // opcional

                    // Buscar comparando el primer campo (antes de "________"), trim y case-insensitive
                    val productoEncontrado = lista.find { item ->
                        val partes = item.split("________")
                        val first = partes.getOrNull(0)?.trim()
                        first != null && first.equals(id, ignoreCase = true)
                    }

                    if (productoEncontrado == null) {
                        Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val p = productoEncontrado.split("________")

                    if (p.size < 9) {
                        Toast.makeText(requireContext(), "Formato inválido del producto recibido", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etID_Producto.setText(p[0].trim())
                    etNombre.setText(p[1].trim())
                    etDescripcion.setText(p[2].trim())
                    etPrecio.setText(p[3].trim())
                    etStockMin.setText(p[4].trim())
                    etCategoria.setText(p[5].trim())
                    etEstado.setText(p[6].trim())
                    etGama.setText(p[7].trim())
                    etFoto.setText(p[8].trim())

                    Toast.makeText(requireContext(), "Producto cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ============================================================
    // PUT: ACTUALIZAR PRODUCTO
    // ============================================================

    private fun actualizarProducto() {

        val id = etID_Producto.text.toString()

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = Producto(
            ID_Producto     = id,
            Nombre_Producto = etNombre.text.toString(),
            Descripcion     = etDescripcion.text.toString(),
            Precio_Venta    = etPrecio.text.toString(),
            Stock_Minimo    = etStockMin.text.toString(),
            ID_Categoria    = etCategoria.text.toString(),
            ID_Estado       = etEstado.text.toString(),
            ID_Gama         = etGama.text.toString(),
            Fotos           = etFoto.text.toString()
        )

        // Llamada PUT
        RetroFitInstance.api2kotlin.actualizarProducto("null", id, producto)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
