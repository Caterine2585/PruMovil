package com.example.mimovil.Acciones.Ventas

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

class ventaseliminar : Fragment() {

    private lateinit var etIDVenta: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultado: TextView

    private var idEncontrado: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_venta, container, false)

        etIDVenta = view.findViewById(R.id.etIDVentaEliminar)
        btnBuscar = view.findViewById(R.id.btnBuscarVenta)
        btnEliminar = view.findViewById(R.id.btnEliminarVenta)
        btnOpciones = view.findViewById(R.id.btnOpcionesVentas)
        tvResultado = view.findViewById(R.id.tvResultadoVenta)

        btnBuscar.setOnClickListener { buscarVenta() }
        btnEliminar.setOnClickListener { eliminarVenta() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    private fun buscarVenta() {
        val idventa = etIDVenta.text.toString()

        if (idventa.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el ID de venta", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getVentas()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar ventas", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                     val encontrado = lista.find { it.startsWith("${idventa}_") }

                    if (encontrado == null) {
                        tvResultado.text = "Venta NO encontrada"
                        idEncontrado = null
                        Toast.makeText(requireContext(), "Venta NO encontrada", Toast.LENGTH_SHORT).show()
                    } else {
                        val partes = encontrado.split("________")

                        tvResultado.text = """
                            ID Venta: ${partes.getOrNull(0)}
                            Documento Cliente: ${partes.getOrNull(1)}
                            Documento Empleado: ${partes.getOrNull(2)}
                        """.trimIndent()

                        idEncontrado = idventa
                        Toast.makeText(requireContext(), "Venta encontrada. Puede eliminarla.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun eliminarVenta() {
        val idventa = idEncontrado

        if (idventa == null) {
            Toast.makeText(requireContext(), "Debe buscar la venta primero", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarVenta(idventa)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Venta eliminada correctamente", Toast.LENGTH_SHORT).show()
                        etIDVenta.text.clear()
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

        val view = layoutInflater.inflate(R.layout.opcionventa, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverventa)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrarventa)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizarventa)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminarventa)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ventaseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }
}
