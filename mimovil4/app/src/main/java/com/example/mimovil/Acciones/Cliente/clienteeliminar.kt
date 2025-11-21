package com.example.mimovil.Acciones.Cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class clienteeliminar : Fragment(){


    private lateinit var etDocumentoEliminar: EditText
    private lateinit var btnBuscarEliminar: Button
    private lateinit var btnEliminarCliente: Button
    private lateinit var btnOpciones: ImageButton   // Botón flotante

    private var documentoEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_cliente, container, false)

        etDocumentoEliminar = view.findViewById(R.id.etDocumentoEliminar)
        btnBuscarEliminar = view.findViewById(R.id.btnBuscarEliminar)
        btnEliminarCliente = view.findViewById(R.id.btnEliminarCliente)
        btnOpciones = view.findViewById(R.id.btnOpciones)

        // Buscar cliente antes de eliminar
        btnBuscarEliminar.setOnClickListener { buscarCliente() }

        // Eliminar cliente
        btnEliminarCliente.setOnClickListener { eliminarCliente() }

        // Volver al menú de opciones (bottom sheet)
        btnOpciones.setOnClickListener { abrirOpciones() }

        return view
    }

    // ============================
    //      BUSCAR CLIENTE
    // ============================
    private fun buscarCliente() {

        val documento = etDocumentoEliminar.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el documento", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getClientes()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar clientes", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    // Buscar por coincidencia EXACTA
                    val encontrado = lista.find { it.startsWith("${documento}_") }

                    if (encontrado == null) {
                        Toast.makeText(requireContext(), "Cliente NO encontrado", Toast.LENGTH_SHORT).show()
                        documentoEncontrado = null
                    } else {
                        documentoEncontrado = documento
                        Toast.makeText(requireContext(), "Cliente encontrado. Puede eliminarlo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ============================
    //      ELIMINAR CLIENTE
    // ============================
    private fun eliminarCliente() {

        val documento = documentoEncontrado

        if (documento == null) {
            Toast.makeText(requireContext(), "Debe buscar el cliente primero", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarCliente(documento)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cliente eliminado correctamente", Toast.LENGTH_SHORT).show()
                        etDocumentoEliminar.text.clear()
                        documentoEncontrado = null
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ============================
    //     VOLVER A OPCIONES
    // ============================
    private fun abrirOpciones() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, clienteFragment())
            .addToBackStack(null)
            .commit()
    }
}

