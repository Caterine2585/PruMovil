package com.example.mimovil.Acciones.Empleado

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

class empleadoeliminar : Fragment() {

    private lateinit var etDocumentoEliminarEmp: EditText
    private lateinit var btnBuscarEmpleado: Button
    private lateinit var btnEliminarEmpleado: Button
    private lateinit var btnOpcionesEmpleado: ImageButton
    private lateinit var tvResultadoEmpleado: TextView

    private var documentoEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_empleado, container, false)

        etDocumentoEliminarEmp = view.findViewById(R.id.etDocumentoEliminarEmp)
        btnBuscarEmpleado = view.findViewById(R.id.btnBuscarEmpleado)
        btnEliminarEmpleado = view.findViewById(R.id.btnEliminarEmpleado)
        btnOpcionesEmpleado = view.findViewById(R.id.btnOpcionesEmpleado)
        tvResultadoEmpleado = view.findViewById(R.id.tvResultadoEmpleado)

        btnBuscarEmpleado.setOnClickListener { buscarEmpleado() }
        btnEliminarEmpleado.setOnClickListener { eliminarEmpleado() }
        btnOpcionesEmpleado.setOnClickListener { mostrarMenuOpciones() }

        return view
    }



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

                    val encontrado = lista.find { it.startsWith("${documento}_") }

                    if (encontrado == null) {

                        tvResultadoEmpleado.text = "Empleado NO encontrado"
                        documentoEncontrado = null

                        Toast.makeText(requireContext(), "Empleado NO encontrado", Toast.LENGTH_SHORT).show()

                    } else {

                        val partes = encontrado.split("________")

                        tvResultadoEmpleado.text = """
                                        Documento: ${partes[0]}
                                        Tipo de documento: ${partes[1]}
                                        Nombre: ${partes[2]}${partes[3]}
                                        Edad: ${partes[4]}
                                        Correo: ${partes[5]}
                                        Telefono: ${partes[6]}
                                        Genero: ${partes[7]}
                                        Estado: ${partes[8]}
                                        Rol: ${partes[9]}
                        """.trimIndent()

                        documentoEncontrado = documento
                        Toast.makeText(requireContext(), "Empleado encontrado. Puede eliminarlo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


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
                        tvResultadoEmpleado.text = ""
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



    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opveremp)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistraremp)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizaremp)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminaremp)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoeliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
