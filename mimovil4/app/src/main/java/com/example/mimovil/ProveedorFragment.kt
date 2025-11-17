package com.example.mimovil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Proveedor
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProveedorFragment : Fragment(){

    private lateinit var etIdProveedor: EditText
    private lateinit var etNombreProveedor: EditText
    private lateinit var etCorreoProveedor: EditText
    private lateinit var etTelefonoProveedor: EditText
    private lateinit var etEstadoProveedor: EditText
    private lateinit var btnCrearProveedor: Button
    private lateinit var btnMostrarProveedores: Button
    private lateinit var btnActualizarProveedor: Button
    private lateinit var btnEliminarProveedor: Button
    private lateinit var tvResultadoProveedores: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_proveedor, container, false)

        // Inicializar campos
        etIdProveedor = view.findViewById(R.id.etid_Proveedor)
        etNombreProveedor = view.findViewById(R.id.etNombreP)
        etCorreoProveedor = view.findViewById(R.id.etCorreoProveedor)
        etTelefonoProveedor = view.findViewById(R.id.etTelefonoProveedor)
        etEstadoProveedor = view.findViewById(R.id.etID_EstadoProveedor)

        btnCrearProveedor = view.findViewById(R.id.btnCrearProveedor)
        btnMostrarProveedores = view.findViewById(R.id.btnMostrarProveedor)
        btnActualizarProveedor = view.findViewById(R.id.btnActualizarProveedor)
        btnEliminarProveedor = view.findViewById(R.id.btnEliminarProveedor)

        tvResultadoProveedores = view.findViewById(R.id.tvResultadoProveedor)

        // Eventos
        btnCrearProveedor.setOnClickListener { crearProveedor() }
        btnMostrarProveedores.setOnClickListener { mostrarProveedores() }
        btnActualizarProveedor.setOnClickListener { actualizarProveedor() }
        btnEliminarProveedor.setOnClickListener { eliminarProveedor() }

        return view
    }

    // POST: Crear proveedor
    private fun crearProveedor() {
        val proveedor = Proveedor(
            id_proveedor = etIdProveedor.text.toString().trim(),
            nombre_proveedor = etNombreProveedor.text.toString().trim(),
            correo_proveedor = etCorreoProveedor.text.toString().trim(),
            telefono = etTelefonoProveedor.text.toString().trim(),
            id_estado = etEstadoProveedor.text.toString().trim().ifEmpty { "EST001" }
        )

        if (proveedor.id_proveedor.isEmpty() || proveedor.nombre_proveedor.isEmpty()) {
            Toast.makeText(requireContext(), "ID y Nombre son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.crearProveedor(proveedor)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Proveedor creado correctamente", Toast.LENGTH_LONG).show()
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

    // GET: Mostrar proveedores
    private fun mostrarProveedores() {
        RetroFitInstance.api2kotlin.getProveedores()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val data = response.body().orEmpty()

                        if (data.isEmpty()) {
                            tvResultadoProveedores.text = "No hay proveedores disponibles."
                        } else {
                            val texto = data.joinToString("\n\n") { item ->
                                val partes = item.split("________")
                                if (partes.size >= 5) {
                                    """
                                    ID Proveedor: ${partes[0]}
                                    Nombre: ${partes[1]}
                                    Correo: ${partes[2]}
                                    Teléfono: ${partes[3]}
                                    Estado: ${partes[4]}
                                    """.trimIndent()
                                } else {
                                    "⚠️ Formato incorrecto: $item"
                                }
                            }
                            tvResultadoProveedores.text = texto
                        }
                    } else {
                        tvResultadoProveedores.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    tvResultadoProveedores.text = "Error de conexión: ${t.message}"
                }
            })
    }

    // PUT: Actualizar proveedor
    private fun actualizarProveedor() {
        val id = etIdProveedor.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        val proveedor = Proveedor(
            id_proveedor = id,
            nombre_proveedor = etNombreProveedor.text.toString(),
            correo_proveedor = etCorreoProveedor.text.toString(),
            telefono = etTelefonoProveedor.text.toString(),
            id_estado = etEstadoProveedor.text.toString()
        )

        RetroFitInstance.api2kotlin.actualizarProveedor(id, proveedor)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Proveedor actualizado correctamente", Toast.LENGTH_LONG).show()
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

    // DELETE: Eliminar proveedor
    private fun eliminarProveedor() {
        val id = etIdProveedor.text.toString().trim()
        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el ID para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.eliminarProveedor(id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Proveedor eliminado correctamente", Toast.LENGTH_LONG).show()
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
        etIdProveedor.text.clear()
        etNombreProveedor.text.clear()
        etCorreoProveedor.text.clear()
        etTelefonoProveedor.text.clear()
        etEstadoProveedor.text.clear()
    }
}