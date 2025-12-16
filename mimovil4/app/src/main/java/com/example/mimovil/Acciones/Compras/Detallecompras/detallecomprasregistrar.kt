package com.example.mimovil.Acciones.Compras.Detallecompras

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
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.DetalleCompras
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class detallecomprasregistrar  : Fragment() {

    private lateinit var etIDEntrada: EditText
    private lateinit var etFecha: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etProveedor: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crear_detallecompras, container, false)

        etIDEntrada = view.findViewById(R.id.etIDEntradaDetalle)
        etFecha = view.findViewById(R.id.etFechaDetalle)
        etCantidad = view.findViewById(R.id.etCantidadDetalle)
        etProveedor = view.findViewById(R.id.etProveedorDetalle)
        btnCrear = view.findViewById(R.id.btnCrearDetalle)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleCrear)

        btnCrear.setOnClickListener { crearDetalle() }


        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }



    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionesdetallecompras, null)
        bottomSheet.setContentView(view)

        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdetallecompras)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardetallecompras)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardetallecompras)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardetallecompras)


        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasFragment())
                .addToBackStack(null)
                .commit()
        }


        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecompraseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }


    private fun crearDetalle() {

        val detalle = DetalleCompras(
            fechaentrada = etFecha.text.toString(),
            cantidad = etCantidad.text.toString(),
            idproveedor = etProveedor.text.toString(),
            identrada = etIDEntrada.text.toString()
        )

        if (detalle.identrada.isEmpty() ||
            detalle.fechaentrada.isEmpty() ||
            detalle.cantidad.isEmpty() ||
            detalle.idproveedor.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearDetalleCompra(detalle)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle creado correctamente", Toast.LENGTH_SHORT).show()
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
        etIDEntrada.text.clear()
        etFecha.text.clear()
        etCantidad.text.clear()
        etProveedor.text.clear()
    }


}
