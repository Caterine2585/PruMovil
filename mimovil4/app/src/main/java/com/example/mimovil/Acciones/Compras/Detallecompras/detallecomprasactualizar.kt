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
import com.example.mimovil.model.DetalleCompras
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class detallecomprasactualizar : Fragment(R.layout.fragment_actulizar_detallecompras) {

    private lateinit var etBuscarIdEntrada: EditText
    private lateinit var etBuscarIdProveedor: EditText

    private lateinit var etIdEntrada: EditText
    private lateinit var etIdProveedor: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etFechaEntrada: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton

    private var idEntradaEncontrado: String? = null
    private var idProveedorEncontrado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actulizar_detallecompras, container, false)

        etBuscarIdEntrada = view.findViewById(R.id.etBuscarIdDetalle)
        etBuscarIdProveedor = view.findViewById(R.id.etBuscarIdProveedor)

        etIdEntrada = view.findViewById(R.id.etIdEntrada)
        etIdProveedor = view.findViewById(R.id.etIdProveedor)
        etCantidad = view.findViewById(R.id.etCantidad)
        etFechaEntrada = view.findViewById(R.id.etFechaEntrada)

        btnBuscar = view.findViewById(R.id.btnBuscarDetalle)
        btnActualizar = view.findViewById(R.id.btnActualizarDetalle)
        btnOpciones = view.findViewById(R.id.btnOpcionesDetalleC)

        btnBuscar.setOnClickListener { buscarDetalle() }
        btnActualizar.setOnClickListener { actualizarDetalle() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    // MENÚ OPCIONES

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
            Toast.makeText(requireContext(), "Ya estás en Actualizar", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
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


    //BUSCAR DETALLE (GET)

    private fun buscarDetalle() {
        val idEntrada = etBuscarIdEntrada.text.toString()
        val idProveedor = etBuscarIdProveedor.text.toString()

        if (idEntrada.isEmpty() || idProveedor.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa ID Entrada y ID Proveedor", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDetalleCompras()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo detalles", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()
                    val detalle = lista.find { item ->
                        val partes = item.split("________")
                        partes.getOrNull(0)?.trim() == idEntrada.trim() &&
                                partes.getOrNull(1)?.trim() == idProveedor.trim()
                    }


                    if (detalle == null) {
                        Toast.makeText(requireContext(), "Detalle no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = detalle.split("________")
                    etIdEntrada.setText(partes.getOrNull(0) ?: "")
                    etIdProveedor.setText(partes.getOrNull(1) ?: "")
                    etCantidad.setText(partes.getOrNull(2) ?: "")
                    etFechaEntrada.setText(partes.getOrNull(3) ?: "")

                    idEntradaEncontrado = partes.getOrNull(0)
                    idProveedorEncontrado = partes.getOrNull(1)

                    Toast.makeText(requireContext(), "Detalle cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    //ACTUALIZAR DETALLE (PUT)

    private fun actualizarDetalle() {
        val idEntrada = idEntradaEncontrado
        val idProveedor = idProveedorEncontrado

        if (idEntrada == null || idProveedor == null) {
            Toast.makeText(requireContext(), "Primero busca un detalle", Toast.LENGTH_SHORT).show()
            return
        }

        val detalle = DetalleCompras(
            fechaentrada = etFechaEntrada.text.toString(),
            cantidad = etCantidad.text.toString(),
            idproveedor = idProveedor,
            identrada = idEntrada
        )

        RetroFitInstance.api2kotlin.actualizarDetalleCompra(idEntrada, idProveedor, detalle)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
