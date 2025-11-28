package com.example.mimovil.Acciones.Proveedores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Proveedor.proveedorFragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class proveedoreliminar : Fragment() {

    private lateinit var etIdProveedorEliminar: EditText
    private lateinit var btnBuscarEliminar: Button
    private lateinit var btnEliminarProveedor: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultadoProveedor: TextView

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
        tvResultadoProveedor = view.findViewById(R.id.tvResultadoProveedor)

        btnBuscarEliminar.setOnClickListener { buscarProveedor() }
        btnEliminarProveedor.setOnClickListener { eliminarProveedor() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    //BUSCAR PROVEEDOR

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

                    val encontrado = lista.find { it.startsWith("${id}________") }

                    if (encontrado == null) {

                        tvResultadoProveedor.text = "Proveedor NO encontrado"
                        idProveedorEncontrado = null
                        Toast.makeText(requireContext(), "Proveedor NO encontrado", Toast.LENGTH_SHORT).show()

                    } else {

                        val partes = encontrado.split("________")

                        tvResultadoProveedor.text = """
                            ID: ${partes.getOrNull(0)}
                            Nombre: ${partes.getOrNull(1)}
                            Teléfono: ${partes.getOrNull(2)}
                            Dirección: ${partes.getOrNull(3)}
                            Correo: ${partes.getOrNull(4)}
                            Estado: ${partes.getOrNull(5)}
                        """.trimIndent()

                        idProveedorEncontrado = id
                        Toast.makeText(requireContext(), "Proveedor encontrado. Puede eliminarlo.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    // ELIMINAR PROVEEDOR

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
                        tvResultadoProveedor.text = ""
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


    //MENÚ DESPLEGABLE

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
