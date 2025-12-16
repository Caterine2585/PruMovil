package com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Devoluciones.DetalleDev.Detalledeveliminar
import com.example.mimovil.Acciones.Ventas.DetalleVentas.Detallevenactualizar
import com.example.mimovil.Acciones.Ventas.Detalleventas.Detallevenregistrar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.DetalleDev
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detalledevactualizar : Fragment(R.layout.fragment_actualizar_detalledevolucion) {

    private lateinit var etBuscarIDDevolucion: EditText
    private lateinit var etBuscarIDVenta: EditText

    private lateinit var etIDDetalleDev: EditText
    private lateinit var etIDDevolucion: EditText
    private lateinit var etIDVenta: EditText
    private lateinit var etCantidadDevuelta: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton

    private var devolucionEncontrada: String? = null
    private var ventaEncontrada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_actualizar_detalledevolucion, container, false)

        etBuscarIDDevolucion = view.findViewById(R.id.etBuscarIDDetalleDev)
        etBuscarIDVenta = view.findViewById(R.id.etBuscarIDVenta)

        etIDDetalleDev = view.findViewById(R.id.etIDDetalleDev)
        etIDDevolucion = view.findViewById(R.id.etIDDevolucion)
        etIDVenta = view.findViewById(R.id.etIDVenta)
        etCantidadDevuelta = view.findViewById(R.id.etCantidadDevuelta)

        btnBuscar = view.findViewById(R.id.btnBuscarDetalleDevolucion)
        btnActualizar = view.findViewById(R.id.btnActualizarDetalleDevolucion)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleDevolucion)

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

        val v = layoutInflater.inflate(R.layout.opciondetalledevolucion, null)
        bottomSheet.setContentView(v)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = v.findViewById<LinearLayout>(R.id.opverdetalledevolucion)
        val opRegistrar = v.findViewById<LinearLayout>(R.id.opregistrardetalledevolucion)
        val opActualizar = v.findViewById<LinearLayout>(R.id.opactualizardetalledevolucion)
        val opEliminar = v.findViewById<LinearLayout>(R.id.opEliminardetalledevolucion)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetalledevFragment())
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
                .replace(R.id.frame_layout, Detalledeveliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }


    private fun buscarDetalle() {
        val idDevolucion = etBuscarIDDevolucion.text.toString()
        val idVenta = etBuscarIDVenta.text.toString()

        if (idDevolucion.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa ID Devolución e ID Venta", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDetalleDev()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo detalle", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val detalle = lista.find { item ->
                        val partes = item.split("________")
                        partes.getOrNull(1)?.trim() == idDevolucion.trim() &&
                                partes.getOrNull(2)?.trim() == idVenta.trim()
                    }

                    if (detalle == null) {
                        Toast.makeText(requireContext(), "Detalle no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = detalle.split("________")

                    etIDDetalleDev.setText(partes.getOrNull(0) ?: "")
                    etIDDevolucion.setText(partes.getOrNull(1) ?: "")
                    etIDVenta.setText(partes.getOrNull(2) ?: "")
                    etCantidadDevuelta.setText(partes.getOrNull(3) ?: "")

                    devolucionEncontrada = partes.getOrNull(1)
                    ventaEncontrada = partes.getOrNull(2)

                    Toast.makeText(requireContext(), "Detalle cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun actualizarDetalle() {
        val idDev = devolucionEncontrada
        val idVenta = ventaEncontrada

        if (idDev == null || idVenta == null) {
            Toast.makeText(requireContext(), "Primero busca un detalle", Toast.LENGTH_SHORT).show()
            return
        }

        val detalle = DetalleDev(
            IDDetalleDev = etIDDetalleDev.text.toString(),
            IDDevolucion = idDev,
            IDVenta = idVenta,
            CantidadDevuelta = etCantidadDevuelta.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarDetalleDev(idDev, idVenta, detalle)
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
