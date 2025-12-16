package com.example.mimovil.Acciones.Ventas.DetalleVentas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Ventas.Detalleventas.DetallevenFragment
import com.example.mimovil.Acciones.Ventas.Detalleventas.Detallevenregistrar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Detalle_Ventas
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detallevenactualizar : Fragment(R.layout.fragment_actualizar_detalleventa) {

    private lateinit var etBuscarIdProducto: EditText
    private lateinit var etBuscarIdVenta: EditText

    private lateinit var etIdProducto: EditText
    private lateinit var etIdVenta: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etFechaSalida: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton

    private var idProductoEncontrado: String? = null
    private var idVentaEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_detalleventa, container, false)

        etBuscarIdProducto = view.findViewById(R.id.etBuscarIdProductoV)
        etBuscarIdVenta = view.findViewById(R.id.etBuscarIdVentaV)

        etIdProducto = view.findViewById(R.id.etIdProductoDetalleV)
        etIdVenta = view.findViewById(R.id.etIdVentaDetalleV)
        etCantidad = view.findViewById(R.id.etCantidadV)
        etFechaSalida = view.findViewById(R.id.etFechaSalidaV)

        btnBuscar = view.findViewById(R.id.btnBuscarDetalleVenta)
        btnActualizar = view.findViewById(R.id.btnActualizarDetalleVenta)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleVenta)

        btnBuscar.setOnClickListener { buscarDetalle() }
        btnActualizar.setOnClickListener { actualizarDetalle() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )
        val view = layoutInflater.inflate(R.layout.opciondetalleventas, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdetalleventas)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardetalleventas)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardetalleventas)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardetalleventas)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetallevenFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detallevenregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detallevenactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalleveneliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    private fun buscarDetalle() {
        val idProducto = etBuscarIdProducto.text.toString()
        val idVenta = etBuscarIdVenta.text.toString()

        if (idProducto.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa ID Producto y ID Venta", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDetalleVentas()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo detalles", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()


                    val detalle = lista.find { item ->
                        val partes = item.split("________")
                        partes.getOrNull(2)?.trim() == idProducto.trim() &&
                                partes.getOrNull(3)?.trim() == idVenta.trim()
                    }

                    if (detalle == null) {
                        Toast.makeText(requireContext(), "Detalle no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = detalle.split("________")

                    etCantidad.setText(partes.getOrNull(0) ?: "")
                    etFechaSalida.setText(partes.getOrNull(1) ?: "")
                    etIdProducto.setText(partes.getOrNull(2) ?: "")
                    etIdVenta.setText(partes.getOrNull(3) ?: "")

                    idProductoEncontrado = partes.getOrNull(2)
                    idVentaEncontrado = partes.getOrNull(3)

                    Toast.makeText(requireContext(), "Detalle cargado correctamente", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun actualizarDetalle() {
        val idProducto = idProductoEncontrado
        val idVenta = idVentaEncontrado

        if (idProducto == null || idVenta == null) {
            Toast.makeText(requireContext(), "Primero busca un detalle", Toast.LENGTH_SHORT).show()
            return
        }

        val detalle = Detalle_Ventas(
            cantidad = etCantidad.text.toString().toInt(),
            fecha_salida = etFechaSalida.text.toString(),
            id_producto = idProducto,
            id_venta = idVenta
        )

        RetroFitInstance.api2kotlin.actualizarDetalleVenta(idProducto, idVenta, detalle)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle actualizado", Toast.LENGTH_SHORT).show()
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
