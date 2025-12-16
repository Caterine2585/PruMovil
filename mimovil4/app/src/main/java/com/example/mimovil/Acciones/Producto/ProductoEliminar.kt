package com.example.mimovil.Acciones.Producto

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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoEliminar : Fragment() {

    private lateinit var etID_Producto_Eliminar: EditText
    private lateinit var btnBuscarProducto: Button
    private lateinit var btnEliminarProducto: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultadoProducto: TextView

    private var productoEncontrado: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_producto, container, false)

        etID_Producto_Eliminar = view.findViewById(R.id.etID_ProductoEliminar)
        btnBuscarProducto = view.findViewById(R.id.btnBuscarProducto)
        btnEliminarProducto = view.findViewById(R.id.btnEliminarProducto)
        btnOpciones = view.findViewById(R.id.btnOpcionesProducto)
        tvResultadoProducto = view.findViewById(R.id.tvResultadoProducto)

        btnBuscarProducto.setOnClickListener { buscarProducto() }
        btnEliminarProducto.setOnClickListener { eliminarProducto() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    private fun buscarProducto() {

        val id = etID_Producto_Eliminar.text.toString().trim()

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el ID del producto", Toast.LENGTH_SHORT)
                .show()
            return
        }

        RetroFitInstance.api2kotlin.getProducto("Bearer " + obtenerToken())
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Error al consultar productos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val encontrado = lista.find { it.startsWith("${id}_") }

                    if (encontrado == null) {

                        tvResultadoProducto.text = "Producto NO encontrado"
                        productoEncontrado = null
                        Toast.makeText(
                            requireContext(),
                            "Producto NO encontrado",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        val p = encontrado.split("________")

                        tvResultadoProducto.text = """
                            ID_Producto: ${p.getOrNull(0)}
                            Nombre: ${p.getOrNull(1)}
                            Descripción: ${p.getOrNull(2)}
                            Precio Venta: ${p.getOrNull(3)}
                            Stock Mínimo: ${p.getOrNull(4)}
                            Categoría: ${p.getOrNull(5)}
                            Estado: ${p.getOrNull(6)}
                            Gama: ${p.getOrNull(7)}
                            Foto: ${p.getOrNull(8)}
                        """.trimIndent()

                        productoEncontrado = id
                        Toast.makeText(
                            requireContext(),
                            "Producto encontrado. Puede eliminarlo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }



    private fun eliminarProducto() {

        val id = productoEncontrado

        if (id == null) {
            Toast.makeText(requireContext(), "Debe buscar el producto primero", Toast.LENGTH_SHORT)
                .show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarProducto("Bearer " + obtenerToken(), id)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Producto eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        etID_Producto_Eliminar.text.clear()
                        tvResultadoProducto.text = ""
                        productoEncontrado = null
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }


    private fun obtenerToken(): String {
        val prefs =
            requireContext().getSharedPreferences("usuario", android.content.Context.MODE_PRIVATE)
        return prefs.getString("jwt_token", "") ?: ""
    }


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
}
