package com.example.mimovil.Acciones.Cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class clienteeliminar : Fragment() {

    private lateinit var etDocumentoEliminar: EditText
    private lateinit var btnBuscarEliminar: Button
    private lateinit var btnEliminarCliente: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultadoCliente: TextView   // ⬅ NUEVO

    private var documentoEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_cliente, container, false)

        etDocumentoEliminar = view.findViewById(R.id.etDocumentoEliminar)
        btnBuscarEliminar = view.findViewById(R.id.btnBuscarCliente)
        btnEliminarCliente = view.findViewById(R.id.btnEliminarCliente)
        btnOpciones = view.findViewById(R.id.btnOpciones)
        tvResultadoCliente = view.findViewById(R.id.tvResultadoCliente) // ⬅ NUEVO

        btnBuscarEliminar.setOnClickListener { buscarCliente() }
        btnEliminarCliente.setOnClickListener { eliminarCliente() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    // BUSCAR CLIENTE

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


                    val encontrado = lista.find { it.startsWith("${documento}_") }

                    if (encontrado == null) {

                        tvResultadoCliente.text = "Cliente NO encontrado"

                        documentoEncontrado = null
                        Toast.makeText(requireContext(), "Cliente NO encontrado", Toast.LENGTH_SHORT).show()

                    } else {

                        val partes = encontrado.split("________")

                        tvResultadoCliente.text = """
                            Documento: ${partes.getOrNull(0)}
                            Nombre: ${partes.getOrNull(1)}
                            Apellido:${partes.getOrNull(2)}
                            Teléfono: ${partes.getOrNull(3)}
                            Fecha de nacimiento: ${partes.getOrNull(4)}
                            Genero: ${partes.getOrNull(5)}
                            Estado: ${partes.getOrNull(6)}
                        """.trimIndent()

                        documentoEncontrado = documento
                        Toast.makeText(requireContext(), "Cliente encontrado. Puede eliminarlo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    //ELIMINAR CLIENTE

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
                        tvResultadoCliente.text = ""
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


    //MENÚ DESPLEGABLE

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
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
