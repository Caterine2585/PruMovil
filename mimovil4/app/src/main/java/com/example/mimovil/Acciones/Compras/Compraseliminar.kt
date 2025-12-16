package com.example.mimovil.Acciones.Compras

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

class Compraseliminar : Fragment() {

    private lateinit var etIDEntrada: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultado: TextView

    private var idEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_eliminar_compras, container, false)

        etIDEntrada = view.findViewById(R.id.etIDEntradaEliminar)
        btnBuscar = view.findViewById(R.id.btnBuscarCompra)
        btnEliminar = view.findViewById(R.id.btnEliminarCompra)
        btnOpciones = view.findViewById(R.id.btnOpcionesCompras)
        tvResultado = view.findViewById(R.id.tvResultadoCompra)

        btnBuscar.setOnClickListener { buscarCompra() }
        btnEliminar.setOnClickListener { eliminarCompra() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }



    private fun buscarCompra() {
        val identrada = etIDEntrada.text.toString()

        if (identrada.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el ID de entrada", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getCompras()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar compras", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()
                    val encontrado = lista.find { it.startsWith("${identrada}_") }

                    if (encontrado == null) {
                        tvResultado.text = "Compra NO encontrada"
                        idEncontrado = null
                        Toast.makeText(requireContext(), "Compra NO encontrada", Toast.LENGTH_SHORT).show()
                    } else {
                        val partes = encontrado.split("________")
                        tvResultado.text = """
                            ID Entrada: ${partes.getOrNull(0)}
                            Precio: ${partes.getOrNull(1)}
                            Producto: ${partes.getOrNull(2)}
                            Documento: ${partes.getOrNull(3)}
                        """.trimIndent()
                        idEncontrado = identrada
                        Toast.makeText(requireContext(), "Compra encontrada. Puede eliminarla.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun eliminarCompra() {
        val identrada = idEncontrado

        if (identrada == null) {
            Toast.makeText(requireContext(), "Debe buscar la compra primero", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarCompra(identrada)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Compra eliminada correctamente", Toast.LENGTH_SHORT).show()
                        etIDEntrada.text.clear()
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
        val bottomSheet = BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.opcionescompras, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverCompra)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarCompra)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarCompra)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarCompra)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ComprasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Comprasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Comprasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Compraseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
