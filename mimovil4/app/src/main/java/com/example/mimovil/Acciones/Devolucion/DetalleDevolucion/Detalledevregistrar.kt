package com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Devoluciones.DetalleDev.Detalledeveliminar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.DetalleDev
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detalledevregistrar : Fragment() {

    private lateinit var etIDDetalleDev: EditText
    private lateinit var etIDDevolucion: EditText
    private lateinit var etIDVenta: EditText
    private lateinit var etCantidadDevuelta: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crear_detalledevolucion, container, false)

        etIDDetalleDev = view.findViewById(R.id.etIDDetalleDev)
        etIDDevolucion = view.findViewById(R.id.etIDDevolucion)
        etIDVenta = view.findViewById(R.id.etIDVentaDev)
        etCantidadDevuelta = view.findViewById(R.id.etCantidadDevuelta)
        btnCrear = view.findViewById(R.id.btnCrearDetalleDev)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleCrearDev)

        btnCrear.setOnClickListener { crearDetalleDevolucion() }

        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }



    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val menuView = layoutInflater.inflate(R.layout.opciondetalledevolucion, null)
        bottomSheet.setContentView(menuView)

        val opVer = menuView.findViewById<LinearLayout>(R.id.opverdetalledevolucion)
        val opRegistrar = menuView.findViewById<LinearLayout>(R.id.opregistrardetalledevolucion)
        val opActualizar = menuView.findViewById<LinearLayout>(R.id.opactualizardetalledevolucion)
        val opEliminar = menuView.findViewById<LinearLayout>(R.id.opEliminardetalledevolucion)

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
                .replace(R.id.frame_layout, Detalledevregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledevactualizar())
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



    private fun crearDetalleDevolucion() {

        val detalle = DetalleDev(
            IDDetalleDev = etIDDetalleDev.text.toString(),
            IDDevolucion = etIDDevolucion.text.toString(),
            IDVenta = etIDVenta.text.toString(),
            CantidadDevuelta = etCantidadDevuelta.text.toString()
        )

        if (detalle.IDDetalleDev.isEmpty() ||
            detalle.IDDevolucion.isEmpty() ||
            detalle.IDVenta.isEmpty() ||
            detalle.CantidadDevuelta.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearDevo(detalle)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle de devolución creado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al crear detalle", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun limpiarCampos() {
        etIDDetalleDev.text.clear()
        etIDDevolucion.text.clear()
        etIDVenta.text.clear()
        etCantidadDevuelta.text.clear()
    }
}
