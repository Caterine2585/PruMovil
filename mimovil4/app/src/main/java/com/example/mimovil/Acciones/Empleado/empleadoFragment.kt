package com.example.mimovil.Acciones.Empleado

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class empleadoFragment : Fragment() {

    private val BASE_URL_IMG = "http://192.168.0.11:8080/"

    private lateinit var btnOpciones: ImageButton
    private lateinit var layoutEmpleados: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_empleado, container, false)

        btnOpciones = view.findViewById(R.id.btnOpcionesempleado)
        layoutEmpleados = view.findViewById(R.id.layoutEmpleados)

        mostrarEmpleados()

        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }


    private fun mostrarEmpleados() {

        layoutEmpleados.removeAllViews()

        RetroFitInstance.api2kotlin.getEmpleados()
            .enqueue(object : Callback<List<String>> {

                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val empleados = response.body().orEmpty()

                    if (empleados.isEmpty()) {
                        layoutEmpleados.addView(TextView(requireContext()).apply {
                            text = "No hay empleados registrados."
                            textSize = 16f
                        })
                        return
                    }

                    for (item in empleados) {

                        val partes = item.split("________")

                        if (partes.size < 10) continue

                        val doc = partes[0]
                        val tipoDoc = partes[1]
                        val nombre = partes[2]
                        val apellido = partes[3]
                        val edad = partes[4]
                        val correo = partes[5]
                        val tel = partes[6]
                        val genero = partes[7]
                        val estado = partes[8]
                        val rol = partes[9]
                        val fotoRuta = if (partes.size > 10) partes[10] else ""

                        val contenedor = LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(0, 20, 0, 20)
                        }

                        val info = TextView(requireContext()).apply {
                            text = """
                                Documento: $doc
                                Tipo doc.: $tipoDoc
                                Nombre: $nombre $apellido
                                Edad: $edad
                                Correo: $correo
                                Teléfono: $tel
                                Género: $genero
                                Estado: $estado
                                Rol: $rol
                            """.trimIndent()
                            textSize = 15f
                        }

                        val btnMostrar = Button(requireContext()).apply {
                            text = "Mostrar imagen"
                            textSize = 12f
                        }

                        val img = ImageView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            adjustViewBounds = true
                            maxHeight = (160 * resources.displayMetrics.density).toInt()
                            scaleType = ImageView.ScaleType.FIT_CENTER
                            visibility = View.GONE
                        }



                        btnMostrar.setOnClickListener {
                            if (img.visibility == View.GONE) {

                                if (fotoRuta.isBlank()) {
                                    Toast.makeText(requireContext(), "Este empleado no tiene foto", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                val rutaLimpia = fotoRuta.trim()
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
                                                img.setImageBitmap(bmp)
                                                img.visibility = View.VISIBLE
                                                btnMostrar.text = "Ocultar imagen"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(
                                                requireContext(),
                                                "No se pudo cargar la imagen",
                                                Toast.LENGTH_SHORT
                                            ).show()
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

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
}
