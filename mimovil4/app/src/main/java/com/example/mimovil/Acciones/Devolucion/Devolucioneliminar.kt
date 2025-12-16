package com.example.mimovil.Acciones.Devoluciones

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

class Devolucioneliminar : Fragment() {

    private lateinit var etIDDevolucion: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultado: TextView

    private var idEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_devolucion, container, false)

        etIDDevolucion = view.findViewById(R.id.etIDDevolucionEliminar)
        btnBuscar = view.findViewById(R.id.btnBuscarDevolucion)
        btnEliminar = view.findViewById(R.id.btnEliminarDevolucion)
        btnOpciones = view.findViewById(R.id.btnOpcionesDevolucion)
        tvResultado = view.findViewById(R.id.tvResultadoDevolucion)

        btnBuscar.setOnClickListener { buscarDevolucion() }
        btnEliminar.setOnClickListener { eliminarDevolucion() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }



    private fun buscarDevolucion() {
        val id = etIDDevolucion.text.toString()

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el ID de la devolución", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDevolucion()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar devoluciones", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()
                       val encontrado = lista.find { it.startsWith("${id}________") }

                    if (encontrado == null) {
                        tvResultado.text = "Devolución NO encontrada"
                        idEncontrado = null
                        Toast.makeText(requireContext(), "No existe esta devolución", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = encontrado.split("________")

                    tvResultado.text = """
                        ID Devolución: ${partes.getOrNull(0)}
                        Fecha: ${partes.getOrNull(1)}
                        Motivo: ${partes.getOrNull(2)}
                    """.trimIndent()

                    idEncontrado = id
                    Toast.makeText(requireContext(), "Devolución encontrada", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }



    private fun eliminarDevolucion() {
        val id = idEncontrado

        if (id == null) {
            Toast.makeText(requireContext(), "Debe buscar la devolución primero", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDevolucion(id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Devolución eliminada correctamente", Toast.LENGTH_SHORT).show()
                        etIDDevolucion.text.clear()
                        tvResultado.text = ""
                        idEncontrado = null
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
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opciondevolucion, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdevolucion)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardevolucion)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardevolucion)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardevolucion)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DevolucionFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucionregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucionactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Devolucioneliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
