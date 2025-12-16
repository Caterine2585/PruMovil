package com.example.mimovil.Acciones.Compras.Detallecompras

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

class detallecompraseliminar : Fragment() {

    private lateinit var etIDEntrada: EditText
    private lateinit var etIDProveedor: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnOpciones: ImageButton
    private lateinit var tvResultado: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_eliminar_detallecompras, container, false)

        etIDEntrada = view.findViewById(R.id.etIDEntrada)
        etIDProveedor = view.findViewById(R.id.etIDProveedor)
        btnBuscar = view.findViewById(R.id.btnBuscarDetalle)
        btnEliminar = view.findViewById(R.id.btnEliminarDetalle)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleCompra)
        tvResultado = view.findViewById(R.id.tvResultadoDetalle)

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

        val view = layoutInflater.inflate(R.layout.opcionesdetallecompras, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opverdetallecompras)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistrardetallecompras)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizardetallecompras)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminardetallecompras)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecomprasactualizar())
                .addToBackStack(null)
                .commit()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, detallecompraseliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }


    private fun buscarDetalle() {
        val idEntrada = etIDEntrada.text.toString()
        val idProveedor = etIDProveedor.text.toString()

        if (idEntrada.isEmpty() || idProveedor.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese ambos IDs", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDetalleCompras()
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
                        partes.getOrNull(0) == idEntrada &&
                                partes.getOrNull(1) == idProveedor
                    }


                    if (encontrado == null) {
                        tvResultado.text = "Detalle NO encontrado"
                        return
                    }

                    val partes = encontrado.split("________")

                    tvResultado.text = """
                        ID Entrada: ${partes.getOrNull(0)}
                        ID Proveedor: ${partes.getOrNull(1)}
                        Cantidad: ${partes.getOrNull(2)}
                        Fecha_Entrada: ${partes.getOrNull(3)}
                    """.trimIndent()

                    Toast.makeText(requireContext(), "Detalle encontrado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }



    private fun eliminarDetalle() {
        val idEntrada = etIDEntrada.text.toString()
        val idProveedor = etIDProveedor.text.toString()

        if (idEntrada.isEmpty() || idProveedor.isEmpty()) {
            Toast.makeText(requireContext(), "Debe ingresar ambos IDs", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDetalleCompra(idEntrada, idProveedor)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle eliminado", Toast.LENGTH_SHORT).show()
                        etIDEntrada.text.clear()
                        etIDProveedor.text.clear()
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
