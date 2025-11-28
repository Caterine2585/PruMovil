package com.example.mimovil.Acciones.Compras

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Compras
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

class Comprasregistrar : Fragment(R.layout.fragment_crear_compras) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val etIDEntrada = view.findViewById<EditText>(R.id.etIDEntradaC)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecioCompraC)
        val etIDProducto = view.findViewById<EditText>(R.id.etIDProductoC)
        val etDocumento = view.findViewById<EditText>(R.id.etDocumentoEmpC)


        val btnCrear = view.findViewById<Button>(R.id.btnCrearCompra)
        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpcionesCompras)


        btnCrear.setOnClickListener {
            val compra = Compras(
                identrada = etIDEntrada.text.toString(),
                preciocompra = etPrecio.text.toString(),
                idproducto = etIDProducto.text.toString(),
                documento = etDocumento.text.toString()
            )

            RetroFitInstance.api2kotlin.crearCompra(compra)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Compra registrada", Toast.LENGTH_SHORT).show()
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



    //MENÚ OPCIONES

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionescompras, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverCompra)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarCompra)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarCompra)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarCompra)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ComprasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Comprasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Comprasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Compraseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}

