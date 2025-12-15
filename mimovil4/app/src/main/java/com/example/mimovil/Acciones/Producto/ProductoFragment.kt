package com.example.mimovil.Acciones.Producto

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class ProductoFragment : Fragment() {

    private val BASE_URL = "http://192.168.80.13:8080/"

    private lateinit var btnOpcionesProductos: ImageButton
    private lateinit var layoutProductos: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_producto, container, false)

        btnOpcionesProductos = view.findViewById(R.id.btnOpcionesProducto)
        layoutProductos = view.findViewById(R.id.layoutProductos)

        mostrarProductos()

        btnOpcionesProductos.setOnClickListener {
            mostrarMenuOpciones()
        }

        return view
    }

    // ==================== GET PRODUCTOS ====================
    private fun mostrarProductos() {

        layoutProductos.removeAllViews()

        val prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            layoutProductos.addView(TextView(requireContext()).apply {
                text = "Debes iniciar sesión primero."
            })
            return
        }

        val authHeader = "Bearer $token"

        RetroFitInstance.api2kotlin.getProducto(authHeader)
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val productos = response.body().orEmpty()

                    if (productos.isEmpty()) {
                        layoutProductos.addView(TextView(requireContext()).apply {
                            text = "No hay productos registrados."
                            textSize = 16f
                        })
                        return
                    }

                    for (item in productos) {

                        val partes = item.split("________")
                        if (partes.size < 9) continue

                        val id = partes[0]
                        val nombre = partes[1]
                        val descripcion = partes[2]
                        val precio = partes[3]
                        val stock = partes[4]
                        val categoria = partes[5]
                        val estado = partes[6]
                        val gama = partes[7]
                        val fotoRuta = partes[8]

                        val contenedor = LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(0, 20, 0, 20)
                        }

                        val info = TextView(requireContext()).apply {
                            text = """
                                ID: $id
                                Nombre: $nombre
                                Descripción: $descripcion
                                Precio: $precio
                                Stock mínimo: $stock
                                Categoría: $categoria
                                Estado: $estado
                                Gama: $gama
                            """.trimIndent()
                            textSize = 15f
                        }

                        val btnMostrar = Button(requireContext()).apply {
                            text = "Mostrar imagen"
                            textSize = 12f
                        }

                        val img = ImageView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            adjustViewBounds = true
                            maxHeight = (160 * resources.displayMetrics.density).toInt()
                            scaleType = ImageView.ScaleType.FIT_CENTER
                            visibility = View.GONE
                        }

                        // ===================== IMAGEN (BASE64 + URL + RUTAS) =====================
                        btnMostrar.setOnClickListener {
                            if (img.visibility == View.GONE) {

                                if (fotoRuta.isBlank()) {
                                    Toast.makeText(requireContext(), "Este producto no tiene imagen", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                // Normalizar
                                var ruta = fotoRuta.trim()
                                    .replace("\\", "/")
                                    .removePrefix("/")

                                // 1) Base64
                                val pareceBase64 = ruta.startsWith("data:image") || (ruta.length > 300 && !ruta.startsWith("http"))
                                if (pareceBase64) {
                                    try {
                                        val coma = ruta.indexOf(',')
                                        val soloBase64 = if (coma != -1) ruta.substring(coma + 1) else ruta
                                        val bytes = android.util.Base64.decode(soloBase64, android.util.Base64.DEFAULT)
                                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                        img.setImageBitmap(bmp)
                                        img.visibility = View.VISIBLE
                                        btnMostrar.text = "Ocultar imagen"
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(requireContext(), "Base64 inválido en imagen", Toast.LENGTH_SHORT).show()
                                    }
                                    return@setOnClickListener
                                }

                                // 2) Si viene URL completa pero con localhost/127 -> reemplazar host
                                fun fixHostIfLocal(url: String): String {
                                    return url
                                        .replace("http://localhost:8080/", BASE_URL)
                                        .replace("http://127.0.0.1:8080/", BASE_URL)
                                        .replace("http://10.0.2.2:8080/", BASE_URL) // emulador
                                }

                                val candidatos: List<String> = if (ruta.startsWith("http")) {
                                    listOf(fixHostIfLocal(ruta))
                                } else {
                                    // 3) Ruta relativa: probar /uploads/ y raíz /
                                    val r = ruta.removePrefix("uploads/")
                                    listOf(
                                        BASE_URL + "uploads/" + r,
                                        BASE_URL + r
                                    )
                                }

                                Thread {
                                    var cargada = false

                                    for (url in candidatos.distinct()) {
                                        if (cargada) continue

                                        try {
                                            URL(url).openStream().use { input ->
                                                val bmp = BitmapFactory.decodeStream(input)
                                                requireActivity().runOnUiThread {
                                                    img.setImageBitmap(bmp)
                                                    img.visibility = View.VISIBLE
                                                    btnMostrar.text = "Ocultar imagen"
                                                }
                                                cargada = true
                                            }
                                        } catch (_: Exception) {
                                            // intentar siguiente
                                        }
                                    }

                                    if (!cargada) {
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(
                                                requireContext(),
                                                "No se pudo cargar. Probé: ${candidatos.joinToString(" | ")}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }.start()

                            } else {
                                img.visibility = View.GONE
                                btnMostrar.text = "Mostrar imagen"
                            }
                        }

                        contenedor.addView(info)
                        contenedor.addView(btnMostrar)
                        contenedor.addView(img)

                        layoutProductos.addView(contenedor)
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ==================== MENÚ OPCIONES ====================
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
