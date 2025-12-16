package com.example.mimovil.Acciones.Proveedores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Cliente.clienteFragment
import com.example.mimovil.Acciones.Cliente.clienteactualizar
import com.example.mimovil.Acciones.Cliente.clienteeliminar
import com.example.mimovil.Acciones.Cliente.clienteregistrar
import com.example.mimovil.Acciones.Proveedor.proveedorFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Proveedor
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class proveedoractualizar : Fragment() {

    private lateinit var etBuscarId: EditText
    private lateinit var etId: EditText
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEstado: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizarproveedor, container, false)


        etBuscarId = view.findViewById(R.id.etBuscarIdProveedor)
        etId = view.findViewById(R.id.etIdProveedor)
        etNombre = view.findViewById(R.id.etNombreProveedor)
        etCorreo = view.findViewById(R.id.etCorreoProveedor)
        etTelefono = view.findViewById(R.id.etTelefonoProveedor)
        etEstado = view.findViewById(R.id.etEstadoProveedor)


        btnBuscar = view.findViewById(R.id.btnBuscarProveedor)
        btnActualizar = view.findViewById(R.id.btnActualizarProveedor)
        btnOpciones = view.findViewById(R.id.btnOpciones)


        btnBuscar.setOnClickListener { buscarProveedor() }
        btnActualizar.setOnClickListener { actualizarProveedor() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
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


    private fun buscarProveedor() {
        val id = etBuscarId.text.toString()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un ID de proveedor", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getProveedores()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo datos", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()
                    val proveedorEncontrado = lista.find { it.startsWith(id) }

                    if (proveedorEncontrado == null) {
                        Toast.makeText(requireContext(), "Proveedor no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = proveedorEncontrado.split("________")
                    if (partes.size < 5) {
                        Toast.makeText(requireContext(), "Formato inválido", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etId.setText(partes[0])
                    etNombre.setText(partes[1])
                    etCorreo.setText(partes[2])
                    etTelefono.setText(partes[3])
                    etEstado.setText(partes[4])

                    Toast.makeText(requireContext(), "Proveedor cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun actualizarProveedor() {
        val id = etId.text.toString()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero un proveedor", Toast.LENGTH_SHORT).show()
            return
        }

        val proveedor = Proveedor(
            id_proveedor = id,
            nombre_proveedor = etNombre.text.toString(),
            correo_proveedor = etCorreo.text.toString(),
            telefono = etTelefono.text.toString(),
            id_estado = etEstado.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarProveedor(id, proveedor)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Proveedor actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
