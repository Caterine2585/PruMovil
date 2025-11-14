package com.example.mimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Detalle_Ventas
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleVentasFragment : Fragment() {

    private lateinit var etcantidad: EditText
    private lateinit var etfecha_salida: EditText
    private lateinit var etid_producto: EditText
    private lateinit var etid_venta: EditText

    private lateinit var btnCrearDetalleVenta: Button
    private lateinit var btnMostrarDetalleVenta: Button
    private lateinit var btnActualizarDetalleVenta: Button
    private lateinit var btnEliminarDetalleVenta: Button
    private lateinit var tvResultadoDetalleVenta: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_venta, container, false)

        // Inicializar campos
        etcantidad = view.findViewById(R.id.etCantidad)
        etfecha_salida = view.findViewById(R.id.etFechaSalida)
        etid_producto = view.findViewById(R.id.etIDProducto)
        etid_venta = view.findViewById(R.id.etIDVenta)

        btnCrearDetalleVenta = view.findViewById(R.id.btnCrearDetalleVenta)
        btnMostrarDetalleVenta = view.findViewById(R.id.btnMostrarDetalleVenta)
        btnActualizarDetalleVenta = view.findViewById(R.id.btnActualizarDetalleVenta)
        btnEliminarDetalleVenta = view.findViewById(R.id.btnEliminarDetalleVenta)
        tvResultadoDetalleVenta = view.findViewById(R.id.tvResultadoDetalleVenta)

        btnCrearDetalleVenta.setOnClickListener { crearDetalleVenta() }
        btnMostrarDetalleVenta.setOnClickListener { mostrarDetalleVenta() }
        btnActualizarDetalleVenta.setOnClickListener { actualizarDetalleVenta() }
        btnEliminarDetalleVenta.setOnClickListener { eliminarDetalleVenta() }

        return view
    }

    // -----------------------------
    // POST: Crear Detalle Venta
    // -----------------------------
    private fun crearDetalleVenta() {
        if (etcantidad.text.isEmpty() || etid_producto.text.isEmpty() || etid_venta.text.isEmpty()) {
            Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val detalle = Detalle_Ventas(
            cantidad = etcantidad.text.toString().toInt(),
            fecha_salida = etfecha_salida.text.toString().trim(),
            id_producto = etid_producto.text.toString().trim(),
            id_venta = etid_venta.text.toString().trim()
        )

        RetroFitInstance.api2kotlin.crearDetalleVenta(detalle)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle creado correctamente", Toast.LENGTH_LONG).show()
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

    // -----------------------------
    // GET: Mostrar Detalles
    // -----------------------------
    private fun mostrarDetalleVenta() {
        RetroFitInstance.api2kotlin.getDetalleVentas()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()
                        if (data.isEmpty()) {
                            tvResultadoDetalleVenta.text = "No hay detalles registrados."
                        } else {
                            tvResultadoDetalleVenta.text = data.joinToString("\n\n")
                        }
                    } else {
                        tvResultadoDetalleVenta.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoDetalleVenta.text = "Error de conexión: ${t.message}"
                }
            })
    }

    // -----------------------------
    // PUT: Actualizar Detalle
    // -----------------------------
    private fun actualizarDetalleVenta() {
        val idProducto = etid_producto.text.toString().trim()
        val idVenta = etid_venta.text.toString().trim()

        if (idProducto.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "ID_Producto e ID_Venta son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val detalle = Detalle_Ventas(
            cantidad = etcantidad.text.toString().toInt(),
            fecha_salida = etfecha_salida.text.toString(),
            id_producto = idProducto,
            id_venta = idVenta
        )

        RetroFitInstance.api2kotlin.actualizarDetalleVenta(idProducto, idVenta, detalle)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle actualizado", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // -----------------------------
    // DELETE: Eliminar Detalle
    // -----------------------------
    private fun eliminarDetalleVenta() {
        val idProducto = etid_producto.text.toString().trim()
        val idVenta = etid_venta.text.toString().trim()

        if (idProducto.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Completa ambos IDs para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDetalleVenta(idProducto, idVenta)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle eliminado", Toast.LENGTH_LONG).show()
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

    private fun limpiarCampos() {
        etcantidad.text.clear()
        etfecha_salida.text.clear()
        etid_producto.text.clear()
        etid_venta.text.clear()
    }
}
