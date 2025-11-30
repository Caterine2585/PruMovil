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
import com.example.mimovil.model.Devoluciones
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Devolucionactualizar : Fragment(R.layout.fragment_actualizar_devolucion) {

    private lateinit var etBuscarID: EditText

    private lateinit var etIDDevolucion: EditText
    private lateinit var etFechaDevolucion: EditText
    private lateinit var etMotivo: EditText

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnOpciones: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_devolucion, container, false)

        // INPUTS
        etBuscarID = view.findViewById(R.id.etBuscarIdDevolucion)

        etIDDevolucion = view.findViewById(R.id.etIDDevolucion)
        etFechaDevolucion = view.findViewById(R.id.etFechaDevolucion)
        etMotivo = view.findViewById(R.id.etMotivo)

        // BOTONES
        btnBuscar = view.findViewById(R.id.btnBuscarDevolucion)
        btnActualizar = view.findViewById(R.id.btnActualizarDevolucion)
        btnOpciones = view.findViewById(R.id.btnOpcionesDevolucion)

        btnBuscar.setOnClickListener { buscarDevolucion() }
        btnActualizar.setOnClickListener { actualizarDevolucion() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    //   MENÚ OPCIONES
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


    //   BUSCAR DEVOLUCIÓN
    private fun buscarDevolucion() {

        val idBuscar = etBuscarID.text.toString()

        if (idBuscar.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un ID de devolución", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getDevolucion()
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

                    val devolucionEncontrada = lista.find {
                        it.startsWith(idBuscar + "________")
                    }

                    if (devolucionEncontrada == null) {
                        Toast.makeText(requireContext(), "Devolución no encontrada", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = devolucionEncontrada.split("________")

                    if (partes.size < 3) {
                        Toast.makeText(requireContext(), "Formato inválido", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etIDDevolucion.setText(partes[0])
                    etFechaDevolucion.setText(partes[1])
                    etMotivo.setText(partes[2])

                    Toast.makeText(requireContext(), "Datos cargados", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }


    //  ACTUALIZAR DEVOLUCIÓN (PUT)
    private fun actualizarDevolucion() {

        val id = etIDDevolucion.text.toString()

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero una devolución", Toast.LENGTH_SHORT).show()
            return
        }

        val devolucion = Devoluciones(
            IDDevolucion = id,
            FechaDevolucion = etFechaDevolucion.text.toString(),
            Motivo = etMotivo.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarDevolucion(id, devolucion)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Devolución actualizada", Toast.LENGTH_SHORT).show()
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
