package com.example.mimovil.Acciones.Empleado

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

class empleadoactualizar : Fragment() {

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
    private var fotoBase64: String? = null

    // BOTONES
    private lateinit var btnBuscar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnSeleccionarFoto: Button
    private lateinit var btnOpciones: ImageButton

    // =============================
    //  SELECTOR DE IMAGEN
    // =============================
    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data

                if (uri != null) {
                    imgFoto.setImageURI(uri)

                    val inputStream: InputStream? =
                        requireActivity().contentResolver.openInputStream(uri)

                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    fotoBase64 = convertirABase64(bitmap)
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
                        Toast.makeText(requireContext(), "Error obteniendo datos", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val lista = response.body().orEmpty()

                    val empleadoEncontrado = lista.find {
                        it.startsWith(documento + "_")
                    }

                    if (empleadoEncontrado == null) {
                        Toast.makeText(requireContext(), "Empleado no encontrado", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val partes = empleadoEncontrado.split("________")

                    if (partes.size < 10) {
                        Toast.makeText(requireContext(), "Formato inválido", Toast.LENGTH_SHORT).show()
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

                    // FOTO BASE64 si existe
                    if (partes.size >= 11 && partes[10].isNotEmpty()) {
                        val bytes = Base64.decode(partes[10], Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        imgFoto.setImageBitmap(bmp)
                        fotoBase64 = partes[10]
                    }

                    Toast.makeText(requireContext(), "Empleado cargado", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // ==============================================
    //          ✔ SELECTOR DE IMAGEN
    // ==============================================
    private fun seleccionarFoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        seleccionarImagenLauncher.launch(intent)
    }

    private fun convertirABase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    // ==============================================
    //        ✔ PUT: ACTUALIZAR EMPLEADO
    // ==============================================
    private fun actualizarEmpleado() {

        val documento = etDocumento.text.toString()

        if (documento.isEmpty()) {
            Toast.makeText(requireContext(), "Busca primero un empleado", Toast.LENGTH_SHORT).show()
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
            fotos = fotoBase64 ?: ""
        )

        RetroFitInstance.api2kotlin.actualizarEmpleado(documento, empleado)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Empleado actualizado", Toast.LENGTH_SHORT).show()
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
