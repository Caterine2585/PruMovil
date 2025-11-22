package com.example.mimovil.Acciones.Empleado

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.example.mimovil.model.Empleado
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class empleadoregistrar : Fragment(R.layout.fragment_crear_empleado) {


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

        // BOTÓN CREAR EMPLEADO
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
                fotos = "" // Si quieres agregar foto luego, lo manejas aparte
            )

            val campos = arrayOf(
                etDocumento, etTipoDoc, etNombre, etApellido,
                etEdad, etCorreo, etTelefono, etGenero, etEstado, etRol
            )

            crearEmpleado(empleado, btnCrear, campos)
            val btnSeleccionarFoto = view.findViewById<Button>(R.id.btnSeleccionarFotoC)
            val imgFoto = view.findViewById<ImageView>(R.id.imgFotoEmpC)



        }

        // BOTÓN MENÚ
        btnOpciones.setOnClickListener {
            mostrarMenuOpciones()
        }

    }


    // =============================
    // CREAR EMPLEADO
    // =============================
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

    // =============================
    // MENÚ OPCIONES
    // =============================
    private fun mostrarMenuOpciones() {

        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)

        bottomSheet.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val opRegistrar = view.findViewById<LinearLayout>(R.id.opregistraremp)
        val opActualizar = view.findViewById<LinearLayout>(R.id.opactualizaremp)
        val opEliminar = view.findViewById<LinearLayout>(R.id.opEliminaremp)

        // Registrar → ya estás aquí
        opRegistrar.setOnClickListener { bottomSheet.dismiss() }

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
