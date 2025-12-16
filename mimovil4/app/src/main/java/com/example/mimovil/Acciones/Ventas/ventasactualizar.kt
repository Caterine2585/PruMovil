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
import com.example.mimovil.model.Ventas
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ventasactualizar : Fragment(R.layout.fragment_actualizar_ventas) {

    private lateinit var etBuscarIDVenta: EditText

    private lateinit var etIDVenta: EditText
    private lateinit var etDocumentoCliente: EditText
    private lateinit var etDocumentoEmpleado: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_ventas, container, false)


        etBuscarIDVenta = view.findViewById(R.id.etBuscarIdVenta)

        etIDVenta = view.findViewById(R.id.etIdVenta)
        etDocumentoCliente = view.findViewById(R.id.etDocumentoClienteV)
        etDocumentoEmpleado = view.findViewById(R.id.etDocumentoEmpleadoV)

        btnBuscar = view.findViewById(R.id.btnBuscarVenta)
        btnActualizar = view.findViewById(R.id.btnActualizarVenta)
        btnOpciones = view.findViewById(R.id.btnOpcionesVenta)

        btnBuscar.setOnClickListener { buscarVenta() }
        btnActualizar.setOnClickListener { actualizarVenta() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
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

    private fun buscarVenta() {

        val idBuscar = etBuscarIDVenta.text.toString()

        if (idBuscar.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un ID de venta", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getVentas()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo ventas", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val ventaEncontrada = lista.find {
                        it.startsWith(idBuscar + "_")
                    }

                    if (ventaEncontrada == null) {
                        Toast.makeText(requireContext(), "Venta no encontrada", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = ventaEncontrada.split("________")

                    if (partes.size < 3) {
                        Toast.makeText(requireContext(), "Formato inválido", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etIDVenta.setText(partes[0])
                    etDocumentoCliente.setText(partes[1])
                    etDocumentoEmpleado.setText(partes[2])

                    Toast.makeText(requireContext(), "Venta cargada", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun actualizarVenta() {

        val idVenta = etIDVenta.text.toString()

        if (idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero una venta", Toast.LENGTH_SHORT).show()
            return
        }

        val venta = Ventas(
            id_venta = idVenta,
            documento_cli = etDocumentoCliente.text.toString(),
            documento_emp = etDocumentoEmpleado.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarVenta(idVenta, venta)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Venta actualizada", Toast.LENGTH_SHORT).show()
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
