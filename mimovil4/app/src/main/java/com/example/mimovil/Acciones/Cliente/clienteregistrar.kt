package com.example.mimovil.Acciones.Cliente

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Cliente
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class clienteregistrar : Fragment(R.layout.fragment_crear_cliente) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etDocumento = view.findViewById<EditText>(R.id.etDocumentoC)
        val etNombre = view.findViewById<EditText>(R.id.etNombreC)
        val etApellido = view.findViewById<EditText>(R.id.etApellidoC)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefonoC)
        val etFecha = view.findViewById<EditText>(R.id.etFechaC)
        val etGenero = view.findViewById<EditText>(R.id.etGeneroC)
        val etEstado = view.findViewById<EditText>(R.id.etEstadoC)

        val btnCrear = view.findViewById<Button>(R.id.btnCrearCliente)
        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpciones)

        // ✔ BOTÓN CREAR
        btnCrear.setOnClickListener {
            val cliente = Cliente(
                documento = etDocumento.text.toString(),
                nombre = etNombre.text.toString(),
                apellido = etApellido.text.toString(),
                telefono = etTelefono.text.toString(),
                fecha = etFecha.text.toString(),
                genero = etGenero.text.toString(),
                estado = etEstado.text.toString()
            )

            RetroFitInstance.api2kotlin.crearCliente(cliente)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Cliente creado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al crear", Toast.LENGTH_SHORT).show()
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


    //MENÚ DESPLEGABLE

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )
        val view = layoutInflater.inflate(R.layout.opcionescliente, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opver)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrar)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizar)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminar)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, clienteeliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

}


