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
import com.example.mimovil.model.Cliente
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class clienteactualizar : Fragment() {

    private lateinit var etBuscarDocumento: EditText

    private lateinit var etDocumento: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etFecha: EditText
    private lateinit var etGenero: EditText
    private lateinit var etEstado: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_cliente, container, false)

        etBuscarDocumento = view.findViewById(R.id.etBuscarDocumento)

        etDocumento = view.findViewById(R.id.etDocumentoC)
        etNombre = view.findViewById(R.id.etNombreC)
        etApellido = view.findViewById(R.id.etApellidoC)
        etTelefono = view.findViewById(R.id.etTelefonoC)
        etFecha = view.findViewById(R.id.etFechaC)
        etGenero = view.findViewById(R.id.etGeneroC)
        etEstado = view.findViewById(R.id.etEstadoC)

        btnBuscar = view.findViewById(R.id.btnBuscar)
        btnActualizar = view.findViewById(R.id.btnActualizarCliente)
        btnOpciones = view.findViewById(R.id.btnOpciones)

        btnBuscar.setOnClickListener { buscarCliente() }
        btnActualizar.setOnClickListener { actualizarCliente() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
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



    //GET

    private fun buscarCliente() {

        val documento = etBuscarDocumento.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un documento", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getClientes()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo datos", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val clienteEncontrado = lista.find {
                        it.startsWith("${documento}_")
                    }

                    if (clienteEncontrado == null) {
                        Toast.makeText(requireContext(), "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = clienteEncontrado.split("________")

                    if (partes.size < 7) {
                        Toast.makeText(requireContext(), "Formato inválido", Toast.LENGTH_SHORT).show()
                        return
                    }


                    etDocumento.setText(partes[0])
                    etNombre.setText(partes[1])
                    etApellido.setText(partes[2])
                    etTelefono.setText(partes[3])
                    etFecha.setText(partes[4])
                    etGenero.setText(partes[5])
                    etEstado.setText(partes[6])

                    Toast.makeText(requireContext(), "Cliente cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    // PUT

    private fun actualizarCliente() {

        val documento = etDocumento.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = Cliente(
            documento = documento,
            nombre = etNombre.text.toString(),
            apellido = etApellido.text.toString(),
            telefono = etTelefono.text.toString(),
            fecha = etFecha.text.toString(),
            genero = etGenero.text.toString(),
            estado = etEstado.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarCliente(documento, cliente)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cliente actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
