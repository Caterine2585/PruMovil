package com.example.mimovil.Acciones.Empleado

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Empleado
import com.example.mimovil.model.EmpleadoActualizarRequest   // ✅ NUEVO IMPORT
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class empleadoactualizar : Fragment() {

    private val BASE_URL_IMG = "http://192.168.0.11:8080/"

    private lateinit var etBuscarDocumento: EditText

    private lateinit var etDocumento: EditText
    private lateinit var etTipoDoc: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etEdad: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etGenero: EditText
    private lateinit var etEstado: EditText
    private lateinit var etRol: EditText

    private lateinit var etContrasena: EditText

    private lateinit var imgFoto: ImageView
    private var fotoBase64: String? = null

    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnSeleccionarFoto: Button
    private lateinit var btnOpciones: ImageButton

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    imgFoto.setImageURI(uri)

                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {

                        fotoBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    } else {
                        Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_actualizar_empleado, container, false)

        etBuscarDocumento = view.findViewById(R.id.etBuscarDocumentoEmp)

        etDocumento = view.findViewById(R.id.etDocumentoEmp)
        etTipoDoc = view.findViewById(R.id.etTipoDocEmp)
        etNombre = view.findViewById(R.id.etNombreEmp)
        etApellido = view.findViewById(R.id.etApellidoEmp)
        etEdad = view.findViewById(R.id.etEdadEmp)
        etCorreo = view.findViewById(R.id.etCorreoEmp)
        etTelefono = view.findViewById(R.id.etTelefonoEmp)
        etGenero = view.findViewById(R.id.etGeneroEmp)
        etEstado = view.findViewById(R.id.etEstadoEmp)
        etRol = view.findViewById(R.id.etRolEmp)

        etContrasena = view.findViewById(R.id.etContrasenaEmp)

        imgFoto = view.findViewById(R.id.imgFotoEmp)

        btnBuscar = view.findViewById(R.id.btnBuscarEmp)
        btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFoto)
        btnActualizar = view.findViewById(R.id.btnActualizarEmpleado)
        btnOpciones = view.findViewById(R.id.btnOpcionesempleado)

        btnBuscar.setOnClickListener { buscarEmpleado() }
        btnSeleccionarFoto.setOnClickListener { seleccionarFoto() }
        btnActualizar.setOnClickListener { actualizarEmpleado() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )
        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        view.findViewById<LinearLayout>(R.id.opveremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.opregistraremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoregistrar())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.opactualizaremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoactualizar())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.opEliminaremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoeliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }


    private fun buscarEmpleado() {
        val documento = etBuscarDocumento.text.toString().trim()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un documento", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getEmpleados()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error obteniendo datos", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val empleadoEncontrado = lista.firstOrNull { row ->
                        val p = row.split("________")
                        p.isNotEmpty() && p[0].trim() == documento
                    }

                    if (empleadoEncontrado == null) {
                        Toast.makeText(requireContext(), "Empleado no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = empleadoEncontrado.split("________")

                    if (partes.size < 10) {
                        Toast.makeText(requireContext(), "Formato inválido desde el servidor", Toast.LENGTH_SHORT).show()
                        return
                    }

                    etDocumento.setText(partes[0])
                    etTipoDoc.setText(partes[1])
                    etNombre.setText(partes[2])
                    etApellido.setText(partes[3])
                    etEdad.setText(partes[4])
                    etCorreo.setText(partes[5])
                    etTelefono.setText(partes[6])
                    etGenero.setText(partes[7])
                    etEstado.setText(partes[8])
                    etRol.setText(partes[9])

                    val rutaFotoRaw = if (partes.size >= 11) partes[10].trim() else ""

                    if (rutaFotoRaw.isNotBlank()) {

                        val rutaLimpia = rutaFotoRaw
                            .replace("\\", "/")
                            .removePrefix("/")

                        val urlFinal = if (rutaLimpia.startsWith("http")) {
                            rutaLimpia
                        } else {
                            BASE_URL_IMG + rutaLimpia
                        }

                        Thread {
                            try {
                                URL(urlFinal).openStream().use { input ->
                                    val bmp = BitmapFactory.decodeStream(input)
                                    requireActivity().runOnUiThread {
                                        imgFoto.setImageBitmap(bmp)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                requireActivity().runOnUiThread {
                                    Toast.makeText(requireContext(), "Error al cargar la foto", Toast.LENGTH_SHORT).show()
                                    imgFoto.setImageDrawable(null)
                                }
                            }
                        }.start()

                    } else {
                        imgFoto.setImageDrawable(null)
                    }

                    fotoBase64 = null


                    etContrasena.setText("")

                    Toast.makeText(requireContext(), "Empleado cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }



    private fun seleccionarFoto() {
        seleccionarImagenLauncher.launch("image/*")
    }


    private fun actualizarEmpleado() {

        val documento = etDocumento.text.toString().trim()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero un empleado", Toast.LENGTH_SHORT).show()
            return
        }

        val request = EmpleadoActualizarRequest(
            tipoDocumento = etTipoDoc.text.toString(),
            nombre = etNombre.text.toString(),
            apellido = etApellido.text.toString(),
            edad = etEdad.text.toString(),
            correo = etCorreo.text.toString(),
            telefono = etTelefono.text.toString(),
            genero = etGenero.text.toString(),
            idEstado = etEstado.text.toString(),
            idRol = etRol.text.toString(),

            fotos = fotoBase64?.takeIf { it.isNotBlank() },

            contrasena = etContrasena.text.toString().takeIf { it.isNotBlank() }
        )

        RetroFitInstance.api2kotlin.actualizarEmpleado(documento, request)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Empleado actualizado", Toast.LENGTH_SHORT).show()

                        etContrasena.setText("")

                        fotoBase64 = null
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
