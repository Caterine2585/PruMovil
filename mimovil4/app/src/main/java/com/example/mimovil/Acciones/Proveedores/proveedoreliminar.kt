package com.example.mimovil.Acciones.Proveedores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Proveedor.proveedorFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class proveedoreliminar : Fragment() {

    private lateinit var etIdProveedorEliminar: EditText
    private lateinit var btnBuscarEliminar: Button
    private lateinit var btnEliminarProveedor: Button
    private lateinit var btnOpciones: ImageButton

    private var idProveedorEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_proveedoreliminar, container, false)

        etIdProveedorEliminar = view.findViewById(R.id.etIdProveedorEliminar)
        btnBuscarEliminar = view.findViewById(R.id.btnBuscarProveedorEliminar)
        btnEliminarProveedor = view.findViewById(R.id.btnEliminarProveedor)
        btnOpciones = view.findViewById(R.id.btnOpciones)

        // Buscar proveedor antes de eliminar
        btnBuscarEliminar.setOnClickListener { buscarProveedor() }

        // Eliminar proveedor
        btnEliminarProveedor.setOnClickListener { eliminarProveedor() }

        // Volver al menú de opciones
        btnOpciones.setOnClickListener { abrirOpciones() }

        return view
    }

    // ============================
    //      BUSCAR PROVEEDOR
    // ============================
    private fun buscarProveedor() {

        val id = etIdProveedorEliminar.text.toString()

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el ID del proveedor", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getProveedores()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar proveedores", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    // Buscar coincidencia exacta
                    val encontrado = lista.find { it.startsWith("${id}________") }

                    if (encontrado == null) {
                        Toast.makeText(requireContext(), "Proveedor NO encontrado", Toast.LENGTH_SHORT).show()
                        idProveedorEncontrado = null
                    } else {
                        idProveedorEncontrado = id
                        Toast.makeText(requireContext(), "Proveedor encontrado. Puede eliminarlo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ============================
    //      ELIMINAR PROVEEDOR
    // ============================
    private fun eliminarProveedor() {

        val id = idProveedorEncontrado

        if (id == null) {
            Toast.makeText(requireContext(), "Debe buscar el proveedor primero", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarProveedor(id)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Proveedor eliminado correctamente", Toast.LENGTH_SHORT).show()
                        etIdProveedorEliminar.text.clear()
                        idProveedorEncontrado = null
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
            .replace(R.id.frame_layout, proveedorFragment())
            .addToBackStack(null)
            .commit()
    }
}
