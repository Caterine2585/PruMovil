package com.example.mimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Compras
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComprasFragment : Fragment(R.layout.fragment_compras) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a los EditText
        val etIDEntrada   = view.findViewById<EditText>(R.id.etIDEntrada)
        val etPrecioCompra = view.findViewById<EditText>(R.id.etPrecioCompra)
        val etIDProducto  = view.findViewById<EditText>(R.id.etIDProducto)
        val etDocumento   = view.findViewById<EditText>(R.id.etDocumentoEmpCompra)
        val btnCrear      = view.findViewById<Button>(R.id.btnCrearCompra)

        btnCrear.setOnClickListener {
            val compra = Compras(
                identrada = etIDEntrada.text.toString().trim(),
                preciocompra = etPrecioCompra.text.toString().trim(),
                idproducto = etIDProducto.text.toString().trim(),
                documento = etDocumento.text.toString().trim()
            )

            // Validaciones simples
            if (compra.identrada.isEmpty() || compra.idproducto.isEmpty()) {
                Toast.makeText(requireContext(), "ID Entrada y Producto son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamada a la API
            RetroFitInstance.api2kotlin.crearCompra(compra)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val body = response.body()?.string().orEmpty()
                            Toast.makeText(requireContext(), "Compra registrada: $body", Toast.LENGTH_LONG).show()
                            limpiarCampos(etIDEntrada, etPrecioCompra, etIDProducto, etDocumento)
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
