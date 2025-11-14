package com.example.mimovil
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.DetalleCompras
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

class DetalleCompraFragment : Fragment(R.layout.fragment_detalle_compras){

    private lateinit var etFechaEntrada: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etIDProveedor: EditText
    private lateinit var etIDEntrada: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnMostrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvResultado: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFechaEntrada = view.findViewById(R.id.etFechaEntrada)
        etCantidad = view.findViewById(R.id.etCantidad)
        etIDProveedor = view.findViewById(R.id.etIDProveedor)
        etIDEntrada = view.findViewById(R.id.etIDEntradaDetC)

        btnCrear = view.findViewById(R.id.btnCrearDetC)
        btnMostrar = view.findViewById(R.id.btnMostrarDetC)
        btnActualizar = view.findViewById(R.id.btnActualizarDetC)
        btnEliminar = view.findViewById(R.id.btnEliminarDetC)
        tvResultado = view.findViewById(R.id.tvResultadoDetC)

        btnCrear.setOnClickListener { crearDetalle() }
        btnMostrar.setOnClickListener { mostrarDetalles() }
        btnActualizar.setOnClickListener { actualizarDetalle() }
        btnEliminar.setOnClickListener { eliminarDetalle() }
    }

    private fun crearDetalle() {
        val detalle = DetalleCompras(
            fechaentrada = etFechaEntrada.text.toString().trim(),
            cantidad = etCantidad.text.toString().trim(),
            idproveedor = etIDProveedor.text.toString().trim(),
            identrada = etIDEntrada.text.toString().trim()
        )

        RetroFitInstance.api2kotlin.crearDetalleCompra(detalle)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle creado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun mostrarDetalles() {
        RetroFitInstance.api2kotlin.getDetalleCompras()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {

                        val lista = response.body().orEmpty()

                        if (lista.isEmpty()) {
                            tvResultado.text = "No hay detalles registrados"
                            return
                        }

                        val texto = lista.joinToString("\n\n") { item ->
                            val p = item.split("________")
                            """
                            Fecha: ${p.getOrNull(0)}
                            Cantidad: ${p.getOrNull(1)}
                            Proveedor: ${p.getOrNull(2)}
                            Entrada: ${p.getOrNull(3)}
                            """.trimIndent()
                        }

                        tvResultado.text = texto
                    }
                }
                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultado.text = "Error: ${t.message}"
                }
            })
    }

    private fun actualizarDetalle() {
        val idEntrada = etIDEntrada.text.toString().trim()
        val idProveedor = etIDProveedor.text.toString().trim()

        val detalle = DetalleCompras(
            fechaentrada = etFechaEntrada.text.toString().trim(),
            cantidad = etCantidad.text.toString().trim(),
            idproveedor = idProveedor,
            identrada = idEntrada
        )

        RetroFitInstance.api2kotlin.actualizarDetalleCompra(idEntrada, idProveedor, detalle)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle actualizado", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun eliminarDetalle() {
        val idEntrada = etIDEntrada.text.toString().trim()
        val idProveedor = etIDProveedor.text.toString().trim()

        RetroFitInstance.api2kotlin.eliminarDetalleCompra(idEntrada, idProveedor)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle eliminado", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun limpiarCampos() {
        etFechaEntrada.text.clear()
        etCantidad.text.clear()
        etIDProveedor.text.clear()
        etIDEntrada.text.clear()
    }
}