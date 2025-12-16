package com.example.mimovil.Acciones.Producto

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Producto
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class ProductoActualizar : Fragment() {

    private val BASE_URL = "http://192.168.0.11:8080/"

    private lateinit var etBuscarID: EditText
    private lateinit var etID: EditText
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText
    private lateinit var etCategoria: EditText
    private lateinit var etEstado: EditText
    private lateinit var etGama: EditText

    private lateinit var imgPreview: ImageView
    private lateinit var btnSeleccionarFoto: Button

    private var fotoBase64Nueva: String? = null

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    imgPreview.setImageURI(uri)
                    imgPreview.visibility = View.VISIBLE

                    val input = requireContext().contentResolver.openInputStream(uri)
                    val bytes = input?.readBytes()
                    input?.close()

                    if (bytes != null) {
                        fotoBase64Nueva = "data:image/jpeg;base64," +
                                Base64.encodeToString(bytes, Base64.NO_WRAP)
                    } else {
                        Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_actualizar_producto, container, false)

        etBuscarID = view.findViewById(R.id.etBuscarID_Producto)
        etID = view.findViewById(R.id.etID_Producto)
        etNombre = view.findViewById(R.id.etNombre_Producto)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etPrecio = view.findViewById(R.id.etPrecio_Venta)
        etStock = view.findViewById(R.id.etStock_Minimo)
        etCategoria = view.findViewById(R.id.etID_Categoria)
        etEstado = view.findViewById(R.id.etID_Estado)
        etGama = view.findViewById(R.id.etID_Gama)

        imgPreview = view.findViewById(R.id.imgProductoPreview)
        btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFotoProducto)

        btnBuscar = view.findViewById(R.id.btnBuscarProducto)
        btnActualizar = view.findViewById(R.id.btnActualizarProducto)
        btnOpciones = view.findViewById(R.id.btnOpcionesProducto)

        btnBuscar.setOnClickListener { buscarProducto() }
        btnActualizar.setOnClickListener { actualizarProducto() }
        btnSeleccionarFoto.setOnClickListener { seleccionarImagenLauncher.launch("image/*") }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    private fun buscarProducto() {
        val id = etBuscarID.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un ID", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getProducto("Bearer $token")
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val producto = response.body().orEmpty().firstOrNull { row ->
                        val p = row.split("________")
                        p.isNotEmpty() && p[0].trim().equals(id, true)
                    }

                    if (producto == null) {
                        Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val p = producto.split("________")
                    if (p.size < 9) {
                        Toast.makeText(requireContext(), "Formato inválido desde servidor", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etID.setText(p[0])
                    etNombre.setText(p[1])
                    etDescripcion.setText(p[2])
                    etPrecio.setText(p[3])
                    etStock.setText(p[4])
                    etCategoria.setText(p[5])
                    etEstado.setText(p[6])
                    etGama.setText(p[7])


                    mostrarFotoProducto(p[8])

                    fotoBase64Nueva = null

                    Toast.makeText(requireContext(), "Producto cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun mostrarFotoProducto(fotoRaw: String) {

        val foto = fotoRaw.trim()
        if (foto.isBlank()) {
            imgPreview.setImageDrawable(null)
            imgPreview.visibility = View.GONE
            return
        }

        // 1) Base64
        val pareceBase64 = foto.startsWith("data:image") || (foto.length > 300 && !foto.startsWith("http"))
        if (pareceBase64) {
            try {
                val base64Solo = foto.substringAfter(",", foto)
                val bytes = Base64.decode(base64Solo, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgPreview.setImageBitmap(bmp)
                imgPreview.visibility = View.VISIBLE
            } catch (e: Exception) {
                imgPreview.setImageDrawable(null)
                imgPreview.visibility = View.GONE
            }
            return
        }

        val ruta = foto
            .replace("\\", "/")
            .removePrefix("/")
            .replace("http://localhost:8080/", BASE_URL)
            .replace("http://127.0.0.1:8080/", BASE_URL)
            .replace("http://10.0.2.2:8080/", BASE_URL)

        val urlsAProbar: List<String> =
            if (ruta.startsWith("http")) {
                listOf(ruta)
            } else {
                val r = ruta.removePrefix("uploads/")
                listOf(
                    BASE_URL + "uploads/" + r,
                    BASE_URL + r
                )
            }

        imgPreview.visibility = View.VISIBLE

        Thread {
            var bitmap: android.graphics.Bitmap? = null

            for (u in urlsAProbar.distinct()) {
                try {
                    val input = URL(u).openStream()
                    bitmap = BitmapFactory.decodeStream(input)
                    input.close()

                    if (bitmap != null) {
                        break
                    }
                } catch (_: Exception) {

                }
            }

            requireActivity().runOnUiThread {
                if (bitmap != null) {
                    imgPreview.setImageBitmap(bitmap)
                    imgPreview.visibility = View.VISIBLE
                } else {
                    imgPreview.setImageDrawable(null)
                    imgPreview.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "No se pudo cargar la foto del producto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun actualizarProducto() {

        val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = Producto(
            ID_Producto = etID.text.toString().trim(),
            Nombre_Producto = etNombre.text.toString().trim(),
            Descripcion = etDescripcion.text.toString().trim(),
            Precio_Venta = etPrecio.text.toString().trim(),
            Stock_Minimo = etStock.text.toString().trim(),
            ID_Categoria = etCategoria.text.toString().trim(),
            ID_Estado = etEstado.text.toString().trim(),
            ID_Gama = etGama.text.toString().trim(),
            Fotos = fotoBase64Nueva ?: ""
        )

        RetroFitInstance.api2kotlin.actualizarProducto(
            "Bearer $token",
            producto.ID_Producto,
            producto
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(
                    requireContext(),
                    if (response.isSuccessful) "Producto actualizado"
                    else "Error al actualizar: ${response.code()}",
                    Toast.LENGTH_SHORT
                ).show()
                if (response.isSuccessful) fotoBase64Nueva = null
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )
        val view = layoutInflater.inflate(R.layout.opcionproductos, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        view.findViewById<LinearLayout>(R.id.opverProductos).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductoFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.opregistrarProductos).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductosRegistrar())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.opactualizarProductos).setOnClickListener {
            bottomSheet.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.opEliminarProductos).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductoEliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
