package com.example.mimovil.model

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.io.ByteArrayOutputStream
import java.net.URL

class EmpleadosFragment : Fragment(R.layout.fragment_empleados) {

    // Cambia esto según dónde corre tu API que sirve /uploads/
    private val BASE_URL_IMG = "http://192.168.80.15:8080/" // ej: "http://192.168.0.12:8080/"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etDocumento   = view.findViewById<EditText>(R.id.etDocumentoEmp)
        val etTipoDoc     = view.findViewById<EditText>(R.id.etTipoDocEmp)
        val etNombre      = view.findViewById<EditText>(R.id.etNombreEmp)
        val etApellido    = view.findViewById<EditText>(R.id.etApellidoEmp)
        val etEdad        = view.findViewById<EditText>(R.id.etEdadEmp)
        val etCorreo      = view.findViewById<EditText>(R.id.etCorreoEmp)
        val etTelefono    = view.findViewById<EditText>(R.id.etTelefonoEmp)
        val etGenero      = view.findViewById<EditText>(R.id.etGeneroEmp)
        val etEstado      = view.findViewById<EditText>(R.id.etEstadoEmp)
        val etRol         = view.findViewById<EditText>(R.id.etRolEmp)

        val imgFoto       = view.findViewById<ImageView>(R.id.imgFotoEmp)
        val btnFoto       = view.findViewById<Button>(R.id.btnSeleccionarFoto)

        val btnCrear      = view.findViewById<Button>(R.id.btnCrearEmpleado)
        val btnGet        = view.findViewById<Button>(R.id.btnGetEmpleados)
        val btnActualizar = view.findViewById<Button>(R.id.btnActualizarEmpleado)
        val btnEliminar   = view.findViewById<Button>(R.id.btnEliminarEmpleado)
        val tvEmpleados   = view.findViewById<TextView>(R.id.tvEmpleados)

        // Contenedor dinámico para la lista del GET con botón "Mostrar imagen"
        val layoutEmpleados = view.findViewById<LinearLayout>(R.id.layoutEmpleados)

        // Guardar la imagen seleccionada (Base64)
        var selectedImageBase64: String? = null

        // Seleccionar imagen desde galería
        val imagePicker = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                imgFoto.setImageURI(it)
                selectedImageBase64 = convertirImagenABase64(it)
            }
        }

        btnFoto.setOnClickListener { imagePicker.launch("image/*") }

        // ---------- POST: Crear Empleado ----------
        btnCrear.setOnClickListener {
            val documento = etDocumento.text.toString().trim()
            val nombre    = etNombre.text.toString().trim()

            if (documento.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Documento y Nombre son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val empleado = Empleado(
                documento     = documento,
                tipoDocumento = etTipoDoc.text.toString().trim(),
                nombre        = nombre,
                apellido      = etApellido.text.toString().trim(),
                edad          = etEdad.text.toString().trim(),
                correo        = etCorreo.text.toString().trim(),
                telefono      = etTelefono.text.toString().trim(),
                genero        = etGenero.text.toString().trim(),
                idEstado      = etEstado.text.toString().ifEmpty { "EST001" },
                idRol         = etRol.text.toString().ifEmpty { "ROL002" },
                fotos         = selectedImageBase64 ?: ""
            )

            btnCrear.isEnabled = false
            RetroFitInstance.api2kotlin.crearEmpleado(empleado)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        btnCrear.isEnabled = true
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Empleado creado correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos(etDocumento, etTipoDoc, etNombre, etApellido, etEdad, etCorreo, etTelefono, etGenero, etEstado, etRol)
                            imgFoto.setImageDrawable(null)
                            selectedImageBase64 = null
                        } else {
                            Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        btnCrear.isEnabled = true
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // ---------- GET: Mostrar Empleados (con botón "Mostrar imagen") ----------
        btnGet.setOnClickListener {
            // Limpia vistas anteriores
            layoutEmpleados.removeAllViews()
            tvEmpleados.text = ""

            RetroFitInstance.api2kotlin.getEmpleados()
                .enqueue(object : Callback<List<String>> {
                    override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                        if (response.isSuccessful) {
                            val empleados = response.body().orEmpty()
                            if (empleados.isEmpty()) {
                                tvEmpleados.text = "No hay empleados"
                                return
                            }

                            for (item in empleados) {
                                val p = item.split("________")
                                if (p.size >= 11) {
                                    // Datos
                                    val doc    = p[0]
                                    val nombre = "${p[2]} ${p[3]}"
                                    val correo = p[5]
                                    val tel    = p[6]
                                    val rol    = p[9]
                                    val foto   = p[10] // ruta relativa: uploads/...

                                    // Contenedor vertical por empleado
                                    val contenedor = LinearLayout(requireContext()).apply {
                                        orientation = LinearLayout.VERTICAL
                                        setPadding(0, 16, 0, 16)
                                    }

                                    // Texto info
                                    val info = TextView(requireContext()).apply {
                                        text = """
                                            Documento: $doc
                                            Nombre: $nombre
                                            Correo: $correo
                                            Teléfono: $tel
                                            Rol: $rol
                                        """.trimIndent()
                                        textSize = 15f
                                    }

                                    // Botón "Mostrar imagen"
                                    val btnMostrar = Button(requireContext()).apply {
                                        text = "Mostrar imagen"
                                        textSize = 12f
                                    }

                                    // Imagen (oculta al inicio)
                                    val thumbMaxH = (160 * resources.displayMetrics.density).toInt() // ~160dp

                                    val img = ImageView(requireContext()).apply {
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        adjustViewBounds = true          // respeta proporción
                                        maxHeight = thumbMaxH            // limita el alto (miniatura)
                                        scaleType = ImageView.ScaleType.FIT_CENTER // muestra la imagen completa
                                        visibility = View.GONE
                                    }

                                    btnMostrar.setOnClickListener {
                                        if (img.visibility == View.GONE) {
                                            val url = BASE_URL_IMG + foto
                                            Thread {
                                                try {
                                                    URL(url).openStream().use { input ->
                                                        val bmp = BitmapFactory.decodeStream(input)
                                                        requireActivity().runOnUiThread {
                                                            img.setImageBitmap(bmp)
                                                            img.visibility = View.VISIBLE
                                                            btnMostrar.text = "Ocultar imagen"
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    requireActivity().runOnUiThread {
                                                        Toast.makeText(requireContext(), "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }.start()
                                        } else {
                                            img.visibility = View.GONE
                                            btnMostrar.text = "Mostrar imagen"
                                        }
                                    }

                                    contenedor.addView(info)
                                    contenedor.addView(btnMostrar)
                                    contenedor.addView(img)
                                    layoutEmpleados.addView(contenedor)
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<List<String>>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // ---------- PUT: Actualizar ----------
        btnActualizar.setOnClickListener {
            val emp = Empleado(
                documento     = etDocumento.text.toString().trim(),
                tipoDocumento = etTipoDoc.text.toString().trim(),
                nombre        = etNombre.text.toString().trim(),
                apellido      = etApellido.text.toString().trim(),
                edad          = etEdad.text.toString().trim(),
                correo        = etCorreo.text.toString().trim(),
                telefono      = etTelefono.text.toString().trim(),
                genero        = etGenero.text.toString().trim(),
                idEstado      = etEstado.text.toString().trim().ifEmpty { "EST001" },
                idRol         = etRol.text.toString().trim().ifEmpty { "ROL002" },
                fotos         = selectedImageBase64 ?: "" // opcional
            )

            RetroFitInstance.api2kotlin.actualizarEmpleado(emp.documento, emp)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Toast.makeText(
                            requireContext(),
                            if (response.isSuccessful) "Empleado actualizado" else "Error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // ---------- DELETE: Eliminar ----------
        btnEliminar.setOnClickListener {
            val doc = etDocumento.text.toString().trim()
            if (doc.isEmpty()) {
                Toast.makeText(requireContext(), "Documento obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            RetroFitInstance.api2kotlin.eliminarEmpleado(doc)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Toast.makeText(
                            requireContext(),
                            if (response.isSuccessful) "Empleado eliminado" else "Error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    // Convertir imagen a Base64
    private fun convertirImagenABase64(uri: Uri): String {
        val input: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input)
        val baos = ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun limpiarCampos(vararg ets: EditText) {
        ets.forEach { it.text?.clear() }
    }
}
