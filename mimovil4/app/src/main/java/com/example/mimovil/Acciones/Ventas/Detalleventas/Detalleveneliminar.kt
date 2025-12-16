package com.example.mimovil.Acciones.Ventas.DetalleVentas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Ventas.Detalleventas.DetallevenFragment
import com.example.mimovil.Acciones.Ventas.Detalleventas.Detallevenregistrar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detalleveneliminar : Fragment() {

    private lateinit var etIDProducto: EditText
    private lateinit var etIDVenta: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultado: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_detalleventa, container, false)

        etIDProducto = view.findViewById(R.id.etIDProductoDV)
        etIDVenta = view.findViewById(R.id.etIDVentaDV)
        btnBuscar = view.findViewById(R.id.btnBuscarDetalleVenta)
        btnEliminar = view.findViewById(R.id.btnEliminarDetalleVenta)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleVenta)
        tvResultado = view.findViewById(R.id.tvResultadoDetalleVenta)

        btnBuscar.setOnClickListener { buscarDetalle() }
        btnEliminar.setOnClickListener { eliminarDetalle() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opciondetalleventas, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdetalleventas)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardetalleventas)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardetalleventas)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardetalleventas)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetallevenFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detallevenregistrar())
                .addToBackStack(null)
                .commit()
        }
        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detallevenactualizar())
                .addToBackStack(null)
                .commit()
        }



        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalleveneliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    private fun buscarDetalle() {
        val idProducto = etIDProducto.text.toString()
        val idVenta = etIDVenta.text.toString()

        if (idProducto.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese ambos IDs", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDetalleVentas()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al consultar", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val encontrado = lista.find { item ->
                        val partes = item.split("________")
                        partes.getOrNull(2) == idProducto &&
                                partes.getOrNull(3) == idVenta
                    }

                    if (encontrado == null) {
                        tvResultado.text = "Detalle NO encontrado"
                        return
                    }

                    val partes = encontrado.split("________")

                    tvResultado.text = """
                        Cantidad: ${partes.getOrNull(0)}
                        Fecha Salida: ${partes.getOrNull(1)}
                        ID Producto: ${partes.getOrNull(2)}
                        ID Venta: ${partes.getOrNull(3)}
                    """.trimIndent()

                    Toast.makeText(requireContext(), "Detalle encontrado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun eliminarDetalle() {
        val idProducto = etIDProducto.text.toString()
        val idVenta = etIDVenta.text.toString()

        if (idProducto.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Debe ingresar ambos IDs", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDetalleVenta(idProducto, idVenta)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle eliminado", Toast.LENGTH_SHORT).show()
                        etIDProducto.text.clear()
                        etIDVenta.text.clear()
                        tvResultado.text = ""
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
