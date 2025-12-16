package com.example.mimovil.Acciones.Producto

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.View
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

class ProductosRegistrar : Fragment(R.layout.fragment_crear_productos) {

    private var base64Foto: String? = null
    private lateinit var imgPreview: ImageView


    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        base64Foto =
                            "data:image/jpeg;base64," +
                                    Base64.encodeToString(bytes, Base64.NO_WRAP)

                        imgPreview.setImageURI(uri)
                    } else {
                        Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

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

        val btnCrear    = view.findViewById<Button>(R.id.btnCrearProducto)
        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpcionesProducto)
        val btnImagen   = view.findViewById<Button>(R.id.btnSeleccionarImagen)

        imgPreview = view.findViewById(R.id.imgPreviewProducto)

        btnImagen.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }

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
                Fotos           = base64Foto ?: ""
            )

            if (producto.ID_Producto.isEmpty() || producto.Nombre_Producto.isEmpty()) {
                Toast.makeText(requireContext(), "ID y Nombre son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt_token", null)
            if (token.isNullOrEmpty()) return@setOnClickListener

            val authHeader = "Bearer $token"


            RetroFitInstance.api2kotlin.crearProducto(authHeader, producto)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Producto creado correctamente", Toast.LENGTH_SHORT).show()

                            arrayOf(
                                etID_Producto, etNombre_Producto, etDescripcion, etPrecio_Venta,
                                etStock_Minimo, etID_Categoria, etID_Estado, etID_Gama
                            ).forEach { it.text.clear() }

                            base64Foto = null
                            imgPreview.setImageDrawable(null)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        btnOpciones.setOnClickListener { mostrarMenuOpciones() }
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
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductoActualizar())
                .addToBackStack(null)
                .commit()
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
