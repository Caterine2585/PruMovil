package com.example.mimovil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Producto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductoFragment : Fragment(R.layout.fragment_producto) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            if (producto.ID_Producto.isEmpty() || producto.Nombre_Producto.isEmpty()) {
                Toast.makeText(requireContext(), "ID Producto y Nombre del Producto son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
    }

    private fun limpiarCampos(vararg ets: EditText) {
        ets.forEach { it.text?.clear() }
    }
}