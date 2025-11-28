package com.example.mimovil.Acciones.Devoluciones.DetalleDev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion.DetalledevFragment
import com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion.Detalledevactualizar
import com.example.mimovil.Acciones.Devoluciones.DetalleDevolucion.Detalledevregistrar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detalledeveliminar : Fragment() {

    private lateinit var etIDDevolucion: EditText
    private lateinit var etIDVenta: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultado: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_detalledevolucion, container, false)

        etIDDevolucion = view.findViewById(R.id.etIDDetalleDevDD)
        etIDVenta = view.findViewById(R.id.etIDDevolucionDD)
        btnBuscar = view.findViewById(R.id.btnBuscarDetalleDevolucion)
        btnEliminar = view.findViewById(R.id.btnEliminarDetalleDevolucion)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleDevolucion)
        tvResultado = view.findViewById(R.id.tvResultadoDetalleDevolucion)

        btnBuscar.setOnClickListener { buscarDetalleDev() }
        btnEliminar.setOnClickListener { eliminarDetalleDev() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    // MENÚ OPCIONES
    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opciondetalledevolucion, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdetalledevolucion)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardetalledevolucion)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardetalledevolucion)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardetalledevolucion)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DetalledevFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledevregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledevactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Detalledeveliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    //  BUSCAR DETALLE DEVOLUCIÓN
    private fun buscarDetalleDev() {
        val idDevolucion = etIDDevolucion.text.toString()
        val idVenta = etIDVenta.text.toString()

        if (idDevolucion.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese ambos IDs", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDetalleDev()
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
                        val p = item.split("________")
                        p.getOrNull(1) == idDevolucion &&
                                p.getOrNull(2) == idVenta
                    }

                    if (encontrado == null) {
                        tvResultado.text = "Detalle NO encontrado"
                        return
                    }

                    val p = encontrado.split("________")

                    tvResultado.text = """
                        ID Detalle Devolucion: ${p.getOrNull(0)}
                        ID Devolución: ${p.getOrNull(1)}
                        ID Venta: ${p.getOrNull(2)}
                        Cantidad Devuelta: ${p.getOrNull(3)}
                    """.trimIndent()

                    Toast.makeText(requireContext(), "Detalle encontrado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    //  ELIMINAR DETALLE DEVOLUCIÓN
    private fun eliminarDetalleDev() {
        val idDevolucion = etIDDevolucion.text.toString()
        val idVenta = etIDVenta.text.toString()

        if (idDevolucion.isEmpty() || idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Debe ingresar ambos IDs", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDetalleDev(idDevolucion, idVenta)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle eliminado", Toast.LENGTH_SHORT).show()
                        etIDDevolucion.text.clear()
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
