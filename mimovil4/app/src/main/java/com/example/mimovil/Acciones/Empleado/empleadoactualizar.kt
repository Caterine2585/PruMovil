package com.example.mimovil.Acciones.Empleado

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.graphics.BitmapFactory
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Empleado
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class empleadoactualizar : Fragment() {

    // ⚠️ Cambia esto a la IP/puerto de tu backend
    private val BASE_URL_IMG = "http://192.168.80.17:8080/"

    // INPUTS
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

    // IMAGEN
    private lateinit var imgFoto: ImageView
    private var fotoBase64: String? = null   // solo se llena si el usuario elige NUEVA foto

    // BOTONES
    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnSeleccionarFoto: Button
    private lateinit var btnOpciones: ImageButton

    // =============================
    //  SELECTOR DE IMAGEN (GetContent)
    // =============================
    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    imgFoto.setImageURI(uri)

                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        // NO_WRAP para evitar saltos de línea
                        fotoBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No se pudo leer la imagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar la imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_actualizar_empleado, container, false)

        // =============================
        //  ENLAZAR VISTAS
        // =============================
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

        imgFoto = view.findViewById(R.id.imgFotoEmp)

        btnBuscar = view.findViewById(R.id.btnBuscarEmp)
        btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFoto)
        btnActualizar = view.findViewById(R.id.btnActualizarEmpleado)
        btnOpciones = view.findViewById(R.id.btnOpcionesempleado)

        // =============================
        //     EVENTOS
        // =============================
        btnBuscar.setOnClickListener { buscarEmpleado() }
        btnSeleccionarFoto.setOnClickListener { seleccionarFoto() }
        btnActualizar.setOnClickListener { actualizarEmpleado() }
        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    // =============================
    //      MENÚ OPCIONES
    // =============================
    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)

        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistraremp)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizaremp)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminaremp)

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            Toast.makeText(requireContext(), "Ya estás en Actualizar", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        opEliminar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoeliminar())
                .addToBackStack(null)
                .commit()
        }

        bottomSheet.show()
    }

    // ==============================================
    //          ✔ GET: BUSCAR EMPLEADO
    // ==============================================
    private fun buscarEmpleado() {
        val documento = etBuscarDocumento.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un documento", Toast.LENGTH_SHORT).show()
            return
        }

        RetroFitInstance.api2kotlin.getEmpleados()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Error obteniendo datos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val empleadoEncontrado = lista.find {
                        it.startsWith("${documento}_")
                    }

                    if (empleadoEncontrado == null) {
                        Toast.makeText(
                            requireContext(),
                            "Empleado no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val partes = empleadoEncontrado.split("________")

                    if (partes.size < 10) {
                        Toast.makeText(
                            requireContext(),
                            "Formato inválido desde el servidor",
                            Toast.LENGTH_SHORT
                        ).show()
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

                    // FOTO: ahora tratamos partes[10] como RUTA (uploads/...) y la cargamos desde el backend
                    if (partes.size >= 11 && partes[10].isNotEmpty()) {
                        val rutaFoto = partes[10] // ejemplo: "uploads/empleado_123.jpg"
                        val url = BASE_URL_IMG + rutaFoto

                        // Importante: no ponemos nada en fotoBase64 aquí.
                        // Solo estamos mostrando la foto actual desde el servidor.
                        // fotoBase64 se llenará SOLO si el usuario elige una nueva imagen.

                        Thread {
                            try {
                                URL(url).openStream().use { input ->
                                    val bmp = BitmapFactory.decodeStream(input)
                                    requireActivity().runOnUiThread {
                                        imgFoto.setImageBitmap(bmp)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error al cargar la foto del empleado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    imgFoto.setImageDrawable(null)
                                }
                            }
                        }.start()
                    } else {
                        // No hay foto → dejamos el cuadro gris
                        imgFoto.setImageDrawable(null)
                    }

                    // No tocamos fotoBase64 aquí → sigue null hasta que el usuario seleccione una nueva foto
                    fotoBase64 = null

                    Toast.makeText(
                        requireContext(),
                        "Empleado cargado",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ==============================================
    //          ✔ SELECTOR DE IMAGEN
    // ==============================================
    private fun seleccionarFoto() {
        seleccionarImagenLauncher.launch("image/*")
    }

    // ==============================================
    //        ✔ PUT: ACTUALIZAR EMPLEADO
    // ==============================================
    private fun actualizarEmpleado() {

        val documento = etDocumento.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Busca primero un empleado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val empleado = Empleado(
            documento = documento,
            tipoDocumento = etTipoDoc.text.toString(),
            nombre = etNombre.text.toString(),
            apellido = etApellido.text.toString(),
            edad = etEdad.text.toString(),
            correo = etCorreo.text.toString(),
            telefono = etTelefono.text.toString(),
            genero = etGenero.text.toString(),
            idEstado = etEstado.text.toString(),
            idRol = etRol.text.toString(),
            // Si no se cambia la foto, fotoBase64 sigue null → se manda "" y el backend mantiene la foto actual
            fotos = fotoBase64 ?: ""
        )

        RetroFitInstance.api2kotlin.actualizarEmpleado(documento, empleado)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Empleado actualizado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al actualizar: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
