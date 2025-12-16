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
import com.example.mimovil.model.Compras
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Comprasactualizar: Fragment(R.layout.fragment_actualizar_compras) {

    private lateinit var etBuscarIdEntrada: EditText

    private lateinit var etIdEntrada: EditText
    private lateinit var etPrecioCompra: EditText
    private lateinit var etIdProducto: EditText
    private lateinit var etDocumentoEmpleado: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_compras, container, false)


        etBuscarIdEntrada = view.findViewById<EditText>(R.id.etBuscarIdEntrada)


        etIdEntrada = view.findViewById(R.id.etIdEntrada)
        etPrecioCompra = view.findViewById(R.id.etPrecioCompraC)
        etIdProducto = view.findViewById(R.id.etIdProductoC)
        etDocumentoEmpleado = view.findViewById(R.id.etDocumentoEmpleadoC)


        btnBuscar = view.findViewById(R.id.btnBuscarCompra)
        btnActualizar = view.findViewById(R.id.btnActualizarCompra)
        btnOpciones = view.findViewById(R.id.btnOpciones)


        btnBuscar.setOnClickListener { buscarCompra() }
        btnActualizar.setOnClickListener { actualizarCompra() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

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


    private fun buscarCompra() {

        val idEntradaBuscar = etBuscarIdEntrada.text.toString()

        if (idEntradaBuscar.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un ID de entrada", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getCompras()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo datos", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val compraEncontrada = lista.find {
                        it.startsWith(idEntradaBuscar + "_")
                    }

                    if (compraEncontrada == null) {
                        Toast.makeText(requireContext(), "Compra no encontrada", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = compraEncontrada.split("________")

                    if (partes.size < 4) {
                        Toast.makeText(requireContext(), "Formato inválido", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etIdEntrada.setText(partes[0])
                    etPrecioCompra.setText(partes[1])
                    etIdProducto.setText(partes[2])
                    etDocumentoEmpleado.setText(partes[3])

                    Toast.makeText(requireContext(), "Compra cargada", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun actualizarCompra() {

        val idEntrada = etIdEntrada.text.toString()

        if (idEntrada.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero una compra", Toast.LENGTH_SHORT).show()
            return
        }

        val compra = Compras(
            identrada = idEntrada,
            preciocompra = etPrecioCompra.text.toString(),
            idproducto = etIdProducto.text.toString(),
            documento = etDocumentoEmpleado.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarCompra(idEntrada, compra)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Compra actualizada", Toast.LENGTH_SHORT).show()
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