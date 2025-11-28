package com.example.mimovil.Acciones.Devoluciones

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Devoluciones
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Devolucionregistrar : Fragment(R.layout.fragment_crear_devolucion) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etIDDevolucion = view.findViewById<EditText>(R.id.etIDDevolucion)
        val etFechaDevolucion = view.findViewById<EditText>(R.id.etFechaDevolucion)
        val etMotivo = view.findViewById<EditText>(R.id.etMotivoDevolucion)

        val btnCrear = view.findViewById<Button>(R.id.btnCrearDevolucion)
        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpcionesDevolucion)

        btnCrear.setOnClickListener {
            val devolucion = Devoluciones(
                IDDevolucion = etIDDevolucion.text.toString(),
                FechaDevolucion = etFechaDevolucion.text.toString(),
                Motivo = etMotivo.text.toString()
            )

            RetroFitInstance.api2kotlin.crearDevolucion(devolucion)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Devolución registrada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        btnOpciones.setOnClickListener {
            mostrarMenuOpciones()
        }
    }

    // MENÚ DE OPCIONES PARA DEVOLUCIÓN
    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opciondevolucion, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdevolucion)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardevolucion)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardevolucion)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardevolucion)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DevolucionFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucionregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucionactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucioneliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
