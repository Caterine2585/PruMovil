package com.example.mimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.DetalleDev
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.joinToString
import kotlin.collections.orEmpty

class DetalleDevFragment : Fragment() {

    private lateinit var etID_DetalleDev: EditText
    private lateinit var etID_Devolucion: EditText
    private lateinit var etID_Venta: EditText
    private lateinit var etCantidad_Devuelta: EditText

    private lateinit var btnCrear: Button
    private lateinit var btnMostrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvResultadoDetalleDev: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_dev, container, false)

        etID_DetalleDev = view.findViewById(R.id.etID_DetalleDev)
        etID_Devolucion = view.findViewById(R.id.etID_Devolucion)
        etID_Venta = view.findViewById(R.id.etID_Venta)
        etCantidad_Devuelta = view.findViewById(R.id.etCantidad_Devuelta)

        btnCrear = view.findViewById(R.id.btnCrearDetalleDev)
        btnMostrar = view.findViewById(R.id.btnMostrarDetalleDev)
        btnActualizar = view.findViewById(R.id.btnActualizarDetalleDev)
        btnEliminar = view.findViewById(R.id.btnEliminarDetalleDev)
        tvResultadoDetalleDev = view.findViewById(R.id.tvResultadoDetalleDev)

        btnCrear.setOnClickListener { crearDevo() }
        btnMostrar.setOnClickListener { mostrarDevo() }
        btnActualizar.setOnClickListener { actualizarDevo() }
        btnEliminar.setOnClickListener { eliminarDevo() }

        return view
    }

    private fun crearDevo() {
        val detalledev = DetalleDev(
            IDDetalleDev = etID_DetalleDev.text.toString().trim(),
            IDDevolucion = etID_Devolucion.text.toString().trim(),
            IDVenta = etID_Venta.text.toString().trim(),
            CantidadDevuelta = etCantidad_Devuelta.text.toString().trim(),
            )

        if (detalledev.IDDetalleDev.isEmpty() || detalledev.IDDevolucion.isEmpty() || detalledev.IDVenta.isEmpty() || detalledev.CantidadDevuelta.isEmpty()) {
            Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearDevo(detalledev)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle Devolución creado correctamente", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun mostrarDevo() {
        RetroFitInstance.api2kotlin.getDetalleDev()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoDetalleDev.text = "No hay Detalle Devoluciones disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 4) {
                                    """
                                    ID_DetalleDev: ${partes[0]}
                                    ID_Devolución: ${partes[1]}
                                    ID_Venta :${partes[2]}
                                    Cantidad_Devuelta :${partes[3]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }
                            tvResultadoDetalleDev.text = texto
                        }
                    } else {
                        tvResultadoDetalleDev.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoDetalleDev.text = "Error de conexión: ${t.message}"
                }
            })
    }

    private fun actualizarDevo() {
        val idD = etID_Devolucion.text.toString().trim()
        val idV = etID_Venta.text.toString().trim()
        if (idD.isEmpty() || idV.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID Devolución y ID Venta para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val devo = DetalleDev(
            IDDetalleDev = etID_DetalleDev.text.toString().trim(),
            IDDevolucion = etID_Devolucion.text.toString().trim(),
            IDVenta = etID_Venta.text.toString().trim(),
            CantidadDevuelta = etCantidad_Devuelta.text.toString().trim(),
        )

        RetroFitInstance.api2kotlin.actualizarDetalleDev(idD, idV, devo  )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle Devolución actualizado correctamente", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun eliminarDevo() {
        val idD = etID_Devolucion.text.toString().trim()
        val idV = etID_Venta.text.toString().trim()
        if (idD.isEmpty() || idV.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID Devolución y ID Venta para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDetalleDev(idD, idV)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Detalle Devolución eliminada correctamente", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun limpiarCampos() {
        etID_DetalleDev.text.clear()
        etID_Devolucion.text.clear()
        etID_Venta.text.clear()
        etCantidad_Devuelta.text.clear()
    }

}