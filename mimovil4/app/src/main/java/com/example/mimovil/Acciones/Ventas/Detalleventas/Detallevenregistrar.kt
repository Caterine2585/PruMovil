package com.example.mimovil.Acciones.Ventas.Detalleventas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Ventas.DetalleVentas.Detallevenactualizar
import com.example.mimovil.Acciones.Ventas.DetalleVentas.Detalleveneliminar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Detalle_Ventas
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detallevenregistrar : Fragment() {

    private lateinit var etCantidad: EditText
    private lateinit var etFechaSalida: EditText
    private lateinit var etIDProducto: EditText
    private lateinit var etIDVenta: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crear_detalleventa, container, false)

        etCantidad = view.findViewById(R.id.etCantidadVenta)
        etFechaSalida = view.findViewById(R.id.etFechaSalidaVenta)
        etIDProducto = view.findViewById(R.id.etIDProductoVenta)
        etIDVenta = view.findViewById(R.id.etIDVentaVenta)
        btnCrear = view.findViewById(R.id.btnCrearDetalleVenta)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleCrearVenta)

        btnCrear.setOnClickListener { crearDetalle() }
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

    private fun crearDetalle() {

        val detalle = Detalle_Ventas(
            cantidad = etCantidad.text.toString().toIntOrNull() ?: 0,
            fecha_salida = etFechaSalida.text.toString(),
            id_producto = etIDProducto.text.toString(),
            id_venta = etIDVenta.text.toString()
        )

        if (detalle.cantidad == 0 ||
            detalle.fecha_salida.isEmpty() ||
            detalle.id_producto.isEmpty() ||
            detalle.id_venta.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearDetalleVenta(detalle)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle de venta registrado", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al registrar detalle", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun limpiarCampos() {
        etCantidad.text.clear()
        etFechaSalida.text.clear()
        etIDProducto.text.clear()
        etIDVenta.text.clear()
    }
}
