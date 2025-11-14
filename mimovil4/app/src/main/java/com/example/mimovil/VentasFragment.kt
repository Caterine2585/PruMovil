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
import com.example.mimovil.model.Ventas
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VentasFragment : Fragment() {

    private lateinit var etid_venta: EditText
    private lateinit var etdocumento_cli: EditText
    private lateinit var etdocumento_emp: EditText

    private lateinit var btnCrearVenta: Button
    private lateinit var btnMostrarVenta: Button
    private lateinit var btnActualizarVenta: Button
    private lateinit var btnEliminarVenta: Button
    private lateinit var btnDetalleVenta: Button

    private lateinit var tvResultadoVenta: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ventas, container, false)

        // 🔹 Inicializar campos
        etid_venta = view.findViewById(R.id.etid_venta)
        etdocumento_cli = view.findViewById(R.id.etdocumento_cli)
        etdocumento_emp = view.findViewById(R.id.etdocumento_emp)
        btnCrearVenta = view.findViewById(R.id.btnCrearVenta)
        btnMostrarVenta = view.findViewById(R.id.btnMostrarVenta)
        btnActualizarVenta = view.findViewById(R.id.btnActualizarVenta)
        btnEliminarVenta = view.findViewById(R.id.btnEliminarVenta)
        tvResultadoVenta = view.findViewById(R.id.tvResultadoVenta)
        btnDetalleVenta = view.findViewById(R.id.btndetalleventa)


        // 🔹 Eventos
        btnCrearVenta.setOnClickListener { crearVenta() }
        btnMostrarVenta.setOnClickListener { mostrarVentas() }
        btnActualizarVenta.setOnClickListener { actualizarVenta() }
        btnEliminarVenta.setOnClickListener { eliminarVenta() }
        btnDetalleVenta.setOnClickListener { abrirDetalleVenta() }


        return view
    }

    private fun abrirDetalleVenta() {
        val fragment = DetalleVentasFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }


    // ✅ POST: Crear venta
    private fun crearVenta() {
        val venta = Ventas(
            id_venta = etid_venta.text.toString().trim(),
            documento_cli = etdocumento_cli.text.toString().trim(),
            documento_emp = etdocumento_emp.text.toString().trim()
        )

        if (venta.id_venta.isEmpty() || venta.documento_cli.isEmpty()) {
            Toast.makeText(requireContext(), "Id y Documento Cliente son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearVenta(venta)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Venta creada correctamente", Toast.LENGTH_LONG).show()
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

    // ✅ GET: Mostrar ventas
    private fun mostrarVentas() {
        RetroFitInstance.api2kotlin.getVentas()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoVenta.text = "No hay ventas registradas."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 3) {
                                    """
                                    Id Venta: ${partes[0]}
                                    Documento Cliente: ${partes[1]}
                                    Documento Empleado: ${partes[2]}
                                    """.trimIndent()
                                } else {
                                    "⚠️ Formato incorrecto: $item"
                                }
                            }
                            tvResultadoVenta.text = texto
                        }
                    } else {
                        tvResultadoVenta.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoVenta.text = "Error de conexión: ${t.message}"
                }
            })
    }

    // ✅ PUT: Actualizar venta
    private fun actualizarVenta() {
        val idVenta = etid_venta.text.toString().trim()

        if (idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID de la venta para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val venta = Ventas(
            id_venta = idVenta,
            documento_cli = etdocumento_cli.text.toString(),
            documento_emp = etdocumento_emp.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarVenta(idVenta, venta)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Venta actualizada correctamente", Toast.LENGTH_LONG).show()
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

    // ✅ DELETE: Eliminar venta
    private fun eliminarVenta() {
        val idVenta = etid_venta.text.toString().trim()

        if (idVenta.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarVenta(idVenta)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Venta eliminada correctamente", Toast.LENGTH_LONG).show()
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
        etid_venta.text.clear()
        etdocumento_cli.text.clear()
        etdocumento_emp.text.clear()
    }
}
