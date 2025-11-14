package com.example.mimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Cliente
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClienteFragment : Fragment() {

    private lateinit var etDocumentoC: EditText
    private lateinit var etNombreC: EditText
    private lateinit var etApellidoC: EditText
    private lateinit var etTelefonoC: EditText
    private lateinit var etFechaC: EditText
    private lateinit var etGeneroC: EditText
    private lateinit var etEstadoC: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnMostrarClientes: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvResultadoClientes: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cliente, container, false)

        // 🔹 Inicializar campos
        etDocumentoC = view.findViewById(R.id.etDocumentoC)
        etNombreC = view.findViewById(R.id.etNombreC)
        etApellidoC = view.findViewById(R.id.etApellidoC)
        etTelefonoC = view.findViewById(R.id.etTelefonoC)
        etFechaC = view.findViewById(R.id.etFechaC)
        etGeneroC = view.findViewById(R.id.etGeneroC)
        etEstadoC = view.findViewById(R.id.etEstadoC)
        btnCrear = view.findViewById(R.id.btnCrearCliente)
        btnMostrarClientes = view.findViewById(R.id.btnMostrarClientes)
        btnActualizar = view.findViewById(R.id.btnActualizarCliente)
        btnEliminar = view.findViewById(R.id.btnEliminarCliente)
        tvResultadoClientes = view.findViewById(R.id.tvResultadoClientes)

        // 🔹 Eventos
        btnCrear.setOnClickListener { crearCliente() }
        btnMostrarClientes.setOnClickListener { mostrarClientes() }
        btnActualizar.setOnClickListener { actualizarCliente() }
        btnEliminar.setOnClickListener { eliminarCliente() }

        return view
    }

    // ✅ POST: Crear cliente
    private fun crearCliente() {
        val cliente = Cliente(
            documento = etDocumentoC.text.toString().trim(),
            nombre = etNombreC.text.toString().trim(),
            apellido = etApellidoC.text.toString().trim(),
            telefono = etTelefonoC.text.toString().trim(),
            fecha = etFechaC.text.toString().trim(),
            genero = etGeneroC.text.toString().trim(),
            estado = etEstadoC.text.toString().trim().ifEmpty { "EST001" }
        )

        if (cliente.documento.isEmpty() || cliente.nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Documento y Nombre son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearCliente(cliente)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cliente creado correctamente", Toast.LENGTH_LONG).show()
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

    // ✅ GET: Mostrar clientes
    private fun mostrarClientes() {
        RetroFitInstance.api2kotlin.getClientes()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoClientes.text = "No hay clientes disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 7) {
                                    """
                                    Documento: ${partes[0]}
                                    Nombre: ${partes[1]}
                                    Apellido :${partes[2]}
                                    Teléfono: ${partes[3]}
                                    Fecha Nacimiento: ${partes[4]}
                                    Género: ${partes[5]}
                                    Estado: ${partes[6]}
                                    """.trimIndent()
                                } else {
                                    "⚠️ Formato incorrecto: $item"
                                }
                            }
                            tvResultadoClientes.text = texto
                        }
                    } else {
                        tvResultadoClientes.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoClientes.text = "Error de conexión: ${t.message}"
                }
            })
    }

    // ✅ PUT: Actualizar cliente
    private fun actualizarCliente() {
        val documento = etDocumentoC.text.toString().trim()
        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el documento para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = Cliente(
            documento = documento,
            nombre = etNombreC.text.toString(),
            apellido = etApellidoC.text.toString(),
            telefono = etTelefonoC.text.toString(),
            fecha = etFechaC.text.toString(),
            genero = etGeneroC.text.toString(),
            estado = etEstadoC.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarCliente(documento, cliente)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cliente actualizado correctamente", Toast.LENGTH_LONG).show()
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

    // ✅ DELETE: Eliminar cliente
    private fun eliminarCliente() {
        val documento = etDocumentoC.text.toString().trim()
        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el documento para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarCliente(documento)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cliente eliminado correctamente", Toast.LENGTH_LONG).show()
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
        etDocumentoC.text.clear()
        etNombreC.text.clear()
        etApellidoC.text.clear()
        etTelefonoC.text.clear()
        etFechaC.text.clear()
        etGeneroC.text.clear()
        etEstadoC.text.clear()
    }
}
