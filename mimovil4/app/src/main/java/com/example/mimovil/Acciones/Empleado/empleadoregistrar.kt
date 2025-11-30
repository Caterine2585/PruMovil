package com.example.mimovil.Acciones.Empleado

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.util.Base64
import android.view.WindowManager
import com.example.mimovil.Acciones.Cliente.clienteFragment
import com.example.mimovil.Acciones.Cliente.clienteactualizar
import com.example.mimovil.Acciones.Cliente.clienteeliminar
import com.example.mimovil.Acciones.Cliente.clienteregistrar
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Empleado
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class empleadoregistrar : Fragment(R.layout.fragment_crear_empleado) {
    private var base64Foto: String? = null
    private var imgFotoEmpC: ImageView? = null

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {

                        base64Foto = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        imgFotoEmpC?.setImageURI(uri)
                    } else {
                        Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CAMPOS
        val etDocumento = view.findViewById<EditText>(R.id.etDocumentoEmpC)
        val etTipoDoc = view.findViewById<EditText>(R.id.etTipoDocEmpC)
        val etNombre = view.findViewById<EditText>(R.id.etNombreEmpC)
        val etApellido = view.findViewById<EditText>(R.id.etApellidoEmpC)
        val etEdad = view.findViewById<EditText>(R.id.etEdadEmpC)
        val etCorreo = view.findViewById<EditText>(R.id.etCorreoEmpC)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefonoEmpC)
        val etGenero = view.findViewById<EditText>(R.id.etGeneroEmpC)
        val etEstado = view.findViewById<EditText>(R.id.etEstadoEmpC)
        val etRol = view.findViewById<EditText>(R.id.etRolEmpC)

        val btnCrear = view.findViewById<Button>(R.id.btnCrearEmpleado)
        val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpcionesCrearEmp)

        // FOTO
        val btnSeleccionarFoto = view.findViewById<Button>(R.id.btnSeleccionarFotoC)
        imgFotoEmpC = view.findViewById(R.id.imgFotoEmpC)


        btnSeleccionarFoto.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }


        btnCrear.setOnClickListener {

            val empleado = Empleado(
                documento = etDocumento.text.toString(),
                tipoDocumento = etTipoDoc.text.toString(),
                nombre = etNombre.text.toString(),
                apellido = etApellido.text.toString(),
                edad = etEdad.text.toString(),
                correo = etCorreo.text.toString(),
                telefono = etTelefono.text.toString(),
                genero = etGenero.text.toString(),
                idEstado = etEstado.text.toString(),
                idRol = etRol.text.toString(),

                fotos = base64Foto ?: ""
            )

            val campos = arrayOf(
                etDocumento, etTipoDoc, etNombre, etApellido,
                etEdad, etCorreo, etTelefono, etGenero, etEstado, etRol
            )

            crearEmpleado(empleado, btnCrear, campos)
        }


        btnOpciones.setOnClickListener {
            mostrarMenuOpciones()
        }
    }


    // CREAR EMPLEADO

    private fun crearEmpleado(
        empleado: Empleado,
        btnCrear: Button,
        campos: Array<EditText>
    ) {
        btnCrear.isEnabled = false

        RetroFitInstance.api2kotlin.crearEmpleado(empleado)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    btnCrear.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Empleado creado correctamente", Toast.LENGTH_SHORT).show()
                        campos.forEach { it.text.clear() }
                        // Limpiamos también la foto
                        base64Foto = null
                        imgFotoEmpC?.setImageDrawable(null)

                        // o alguna imagen por defecto
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

    // MENÚ OPCIONES

    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)
        bottomSheet.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opVer = view.findViewById<LinearLayout>(R.id.opveremp)
        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistraremp)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizaremp)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminaremp)

        opVer.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoFragment())
                .addToBackStack(null)
                .commit()
        }

        opRegistrar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoregistrar())
                .addToBackStack(null)
                .commit()
        }

        opActualizar.setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoactualizar())
                .addToBackStack(null)
                .commit()
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

}
