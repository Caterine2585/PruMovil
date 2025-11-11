package com.example.mimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Compras
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComprasFragment : Fragment(R.layout.fragment_compras) {

    private lateinit var etIDEntrada: EditText
    private lateinit var etPrecioCompra: EditText
    private lateinit var etIDProducto: EditText
    private lateinit var etDocumento: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnMostrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvResultado: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Inicializar vistas
        etIDEntrada = view.findViewById(R.id.etIDEntrada)
        etPrecioCompra = view.findViewById(R.id.etPrecioCompra)
        etIDProducto = view.findViewById(R.id.etIDProducto)
        etDocumento = view.findViewById(R.id.etDocumentoEmpCompra)
        btnCrear = view.findViewById(R.id.btnCrearCompra)
        btnMostrar = view.findViewById(R.id.btnMostrarCompras)
        btnActualizar = view.findViewById(R.id.btnActualizarCompra)
        btnEliminar = view.findViewById(R.id.btnEliminarCompra)
        tvResultado = view.findViewById(R.id.tvResultadoCompras)

        //  Eventos
        btnCrear.setOnClickListener { crearCompra() }
        btnMostrar.setOnClickListener { mostrarCompras() }
        btnActualizar.setOnClickListener { actualizarCompra() }
        btnEliminar.setOnClickListener { eliminarCompra() }
    }

    //  POST: Crear compra
    private fun crearCompra() {
        val compra = Compras(
            identrada = etIDEntrada.text.toString().trim(),
            preciocompra = etPrecioCompra.text.toString().trim(),
            idproducto = etIDProducto.text.toString().trim(),
            documento = etDocumento.text.toString().trim()
        )

        if (compra.identrada.isEmpty() || compra.idproducto.isEmpty()) {
            Toast.makeText(requireContext(), "ID Entrada y Producto son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearCompra(compra)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Compra creada correctamente", Toast.LENGTH_LONG).show()
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

    //  GET: Mostrar compras
    private fun mostrarCompras() {
        RetroFitInstance.api2kotlin.getCompras()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultado.text = "No hay compras registradas."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 4) {
                                    """
                                 ID Entrada: ${partes[0]}
                                 Precio: ${partes[1]}
                                 ID Producto: ${partes[2]}
                                 Documento: ${partes[3]}
                                """.trimIndent()
                                } else {
                                    " Formato incorrecto: $item"
                                }
                            }
                            tvResultado.text = texto
                        }
                    } else {
                        tvResultado.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultado.text = "Error de conexión: ${t.message}"
                }
            })
    }


    //  PUT: Actualizar compra
    private fun actualizarCompra() {
        val idEntrada = etIDEntrada.text.toString().trim()
        if (idEntrada.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID Entrada para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val compra = Compras(
            identrada = idEntrada,
            preciocompra = etPrecioCompra.text.toString().trim(),
            idproducto = etIDProducto.text.toString().trim(),
            documento = etDocumento.text.toString().trim()
        )

        RetroFitInstance.api2kotlin.actualizarCompra(idEntrada, compra)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Compra actualizada correctamente", Toast.LENGTH_LONG).show()
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

    // DELETE: Eliminar compra
    private fun eliminarCompra() {
        val idEntrada = etIDEntrada.text.toString().trim()
        if (idEntrada.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID Entrada para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarCompra(idEntrada)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Compra eliminada correctamente", Toast.LENGTH_LONG).show()
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
        etIDEntrada.text.clear()
        etPrecioCompra.text.clear()
        etIDProducto.text.clear()
        etDocumento.text.clear()
    }
}
