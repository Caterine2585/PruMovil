package com.example.mimovil.Acciones.Proveedores

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Proveedor.proveedorFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Proveedor
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Proveedorregistrar : Fragment(R.layout.fragment_crearproveedor) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val etId = view.findViewById<EditText>(R.id.etId_proveedor)
        val etNombre = view.findViewById<EditText>(R.id.etNombreProveedor)
        val etCorreo = view.findViewById<EditText>(R.id.etCorreoProveedor)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefonoProveedor)
        val etEstado = view.findViewById<EditText>(R.id.etEstadoProvee)

        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpcionesProveedor)
        val btnCrear = view.findViewById<Button>(R.id.btnCrearProveedor)


        btnCrear.setOnClickListener {

            if (etId.text.isEmpty() || etNombre.text.isEmpty()) {
                Toast.makeText(requireContext(), "ID y Nombre son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val proveedor = Proveedor(
                id_proveedor = etId.text.toString().trim(),
                nombre_proveedor = etNombre.text.toString().trim(),
                correo_proveedor = etCorreo.text.toString().trim(),
                telefono = etTelefono.text.toString().trim(),
                id_estado = etEstado.text.toString().trim().ifEmpty { "EST001" }
            )

            val campos = arrayOf(etId, etNombre, etCorreo, etTelefono, etEstado)
            crearProveedor(proveedor, btnCrear, campos)
        }

        btnOpciones.setOnClickListener {
            mostrarMenuOpciones()
        }
    }

    private fun crearProveedor(
        proveedor: Proveedor,
        btnCrear: Button,
        campos: Array<EditText>
    ) {
        btnCrear.isEnabled = false

        RetroFitInstance.api2kotlin.crearProveedor(proveedor)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    btnCrear.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Proveedor creado correctamente", Toast.LENGTH_SHORT).show()
                        campos.forEach { it.text.clear() }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    btnCrear.isEnabled = true
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.opcionproveedor, null)

        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverproveedor)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarproveedor)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarproveedor)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarproveedor)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, proveedorFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Proveedorregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, proveedoractualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, proveedoreliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

}
