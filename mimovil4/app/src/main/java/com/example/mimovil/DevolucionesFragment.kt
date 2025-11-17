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
import com.example.mimovil.model.Devoluciones
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.joinToString
import kotlin.collections.orEmpty

class DevolucionesFragment : Fragment() {

    private lateinit var etID_devolucion: EditText
    private lateinit var etFecha_Devolucion: EditText
    private lateinit var etMotivo: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnMostrar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvResultadoDevoluciones: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_devoluciones, container, false)

        etID_devolucion = view.findViewById(R.id.etID_devolucion)
        etFecha_Devolucion = view.findViewById(R.id.etFecha_Devolucion)
        etMotivo = view.findViewById(R.id.etMotivo)

        btnCrear = view.findViewById(R.id.btnCrearDevo)
        btnMostrar = view.findViewById(R.id.btnMostrarDevo)
        btnActualizar = view.findViewById(R.id.btnActualizarDevo)
        btnEliminar = view.findViewById(R.id.btnEliminarDevo)
        tvResultadoDevoluciones = view.findViewById(R.id.tvResultadoDevoluciones)

        btnCrear.setOnClickListener { crearDevo() }
        btnMostrar.setOnClickListener { mostrarDevo() }
        btnActualizar.setOnClickListener { actualizarDevo() }
        btnEliminar.setOnClickListener { eliminarDevo() }

        return view
    }

    private fun crearDevo() {
        val dev = Devoluciones(
            IDDevolucion = etID_devolucion.text.toString().trim(),
            FechaDevolucion = etFecha_Devolucion.text.toString().trim(),
            Motivo = etMotivo.text.toString().trim(),

        )

        if (dev.IDDevolucion.isEmpty() || dev.FechaDevolucion.isEmpty() || dev.Motivo.isEmpty()) {
            Toast.makeText(requireContext(), "ID Devolucion, Fecha Devolucion y el Motivo son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearDevolucion(dev)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Devolución creada correctamente", Toast.LENGTH_LONG).show()
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
        RetroFitInstance.api2kotlin.getDevolucion()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoDevoluciones.text = "No hay Devoluciones disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 7) {
                                    """
                                    ID_Devolucion: ${partes[0]}
                                    Fecha_Devolucion: ${partes[1]}
                                    Motivo :${partes[2]}
                                    """.trimIndent()
                                } else {
                                    "Formato incorrecto: $item"
                                }
                            }
                            tvResultadoDevoluciones.text = texto
                        }
                    } else {
                        tvResultadoDevoluciones.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoDevoluciones.text = "Error de conexión: ${t.message}"
                }
            })
    }

    private fun actualizarDevo() {
        val dev = etID_devolucion.text.toString().trim()
        if (dev.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID Devolución para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val devo = Devoluciones(
            IDDevolucion = etID_devolucion.text.toString().trim(),
            FechaDevolucion = etFecha_Devolucion.text.toString().trim(),
            Motivo = etMotivo.text.toString().trim(),
        )

        RetroFitInstance.api2kotlin.actualizarDevolucion(dev, devo)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Devolución actualizada correctamente", Toast.LENGTH_LONG).show()
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
        val id = etID_devolucion.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID Devolución para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarDevolucion(id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Devolución eliminada correctamente", Toast.LENGTH_LONG).show()
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
        etID_devolucion.text.clear()
        etFecha_Devolucion.text.clear()
        etMotivo.text.clear()
    }

}