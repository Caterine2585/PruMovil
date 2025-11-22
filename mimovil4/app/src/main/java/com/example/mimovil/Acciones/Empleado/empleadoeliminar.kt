package com.example.mimovil.Acciones.Empleado

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

class empleadoeliminar : Fragment() {

    private lateinit var etDocumentoEliminarEmp: EditText
    private lateinit var btnBuscarEmpleado: Button
    private lateinit var btnEliminarEmpleado: Button
    private lateinit var btnOpcionesEmpleado: ImageButton

    private var documentoEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_empleado, container, false)

        // Inputs
        etDocumentoEliminarEmp = view.findViewById(R.id.etDocumentoEliminarEmp)
        btnBuscarEmpleado = view.findViewById(R.id.btnBuscarEmpleado)
        btnEliminarEmpleado = view.findViewById(R.id.btnEliminarEmpleado)
        btnOpcionesEmpleado = view.findViewById(R.id.btnOpcionesEmpleado)

        btnBuscarEmpleado.setOnClickListener { buscarEmpleado() }
        btnEliminarEmpleado.setOnClickListener { eliminarEmpleado() }

        // Botón flotante – regresar al menú empleado
        btnOpcionesEmpleado.setOnClickListener { abrirOpciones() }

        return view
    }

    // =============================
    //       BUSCAR EMPLEADO
    // =============================
    private fun buscarEmpleado() {

        val documento = etDocumentoEliminarEmp.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el documento", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getEmpleados()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar empleados", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    // MISMO FORMATO QUE CLIENTE "documento_____resto"
                    val encontrado = lista.find { it.startsWith("${documento}_") }

                    if (encontrado == null) {
                        documentoEncontrado = null
                        Toast.makeText(requireContext(), "Empleado NO encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        documentoEncontrado = documento
                        Toast.makeText(requireContext(), "Empleado encontrado. Puede eliminarlo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // =============================
    //       ELIMINAR EMPLEADO
    // =============================
    private fun eliminarEmpleado() {

        val documento = documentoEncontrado

        if (documento == null) {
            Toast.makeText(requireContext(), "Debe buscar el empleado primero", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarEmpleado(documento)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Empleado eliminado correctamente", Toast.LENGTH_SHORT).show()

                        etDocumentoEliminarEmp.text.clear()
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

    // =============================
    //        VOLVER A OPCIONES
    // =============================
    private fun abrirOpciones() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, empleadoFragment())
            .addToBackStack(null)
            .commit()
    }
}
