package com.example.mimovil.Acciones.Ventas

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Ventas
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ventasregistrar : Fragment(R.layout.fragment_crear_ventas) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etIDVenta = view.findViewById<EditText>(R.id.etIDVenta)
        val etDocumentoCli = view.findViewById<EditText>(R.id.etDocumentoCliente)
        val etDocumentoEmp = view.findViewById<EditText>(R.id.etDocumentoEmpleado)


        val btnCrear = view.findViewById<Button>(R.id.btnCrearVenta)
        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpcionesVentas)

        btnCrear.setOnClickListener {

            val venta = Ventas(
                id_venta = etIDVenta.text.toString(),
                documento_cli = etDocumentoCli.text.toString(),
                documento_emp = etDocumentoEmp.text.toString()
            )

            RetroFitInstance.api2kotlin.crearVenta(venta)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Venta registrada", Toast.LENGTH_SHORT).show()
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

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionventa, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverventa)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarventa)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarventa)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarventa)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventaseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
