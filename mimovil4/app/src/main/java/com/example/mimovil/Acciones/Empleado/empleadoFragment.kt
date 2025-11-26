package com.example.mimovil.Acciones.Empleado

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mimovil.R
import com.example.mimovil.api.RetroFitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class empleadoFragment : Fragment() {

    // ⚠️ AJUSTA ESTA URL A TU IP / PUERTO DEL BACKEND
    private val BASE_URL_IMG = "http://192.168.80.17:8080/"

    private lateinit var btnOpciones: ImageButton
    private lateinit var layoutEmpleados: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_empleado, container, false)

        btnOpciones = view.findViewById(R.id.btnOpcionesempleado)
        layoutEmpleados = view.findViewById(R.id.layoutEmpleados)

        mostrarEmpleados()

        btnOpciones.setOnClickListener { mostrarMenuOpciones() }

        return view
    }

    // ============================
    //       GET + Mostrar imagen
    // ============================
    private fun mostrarEmpleados() {
        // Limpiamos lo anterior
        layoutEmpleados.removeAllViews()

        RetroFitInstance.api2kotlin.getEmpleados()
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (response.isSuccessful) {
                        val empleados = response.body().orEmpty()

                        if (empleados.isEmpty()) {
                            // Si no hay empleados, mostramos un texto dentro del layout
                            val txt = TextView(requireContext()).apply {
                                text = "No hay empleados registrados."
                                textSize = 16f
                            }
                            layoutEmpleados.addView(txt)
                            return
                        }

                        for (item in empleados) {
                            val p = item.split("________")

                            // Esperamos al menos 11 campos
                            if (p.size >= 11) {
                                val doc = p[0]
                                val nombreCompleto = "${p[2]} ${p[3]}"
                                val correo = p[5]
                                val tel = p[6]
                                val rol = p[9]
                                val fotoRuta = p[10]   // ej: "uploads/empleado_123.jpg"

                                // Contenedor vertical para cada empleado
                                val contenedor = LinearLayout(requireContext()).apply {
                                    orientation = LinearLayout.VERTICAL
                                    setPadding(0, 16, 0, 16)
                                }

                                // Texto info
                                val info = TextView(requireContext()).apply {
                                    text = """
                                        Documento: $doc
                                        Nombre: $nombreCompleto
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

                                // ImageView para la miniatura
                                val thumbMaxH = (160 * resources.displayMetrics.density).toInt()

                                val img = ImageView(requireContext()).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    adjustViewBounds = true
                                    maxHeight = thumbMaxH        // miniatura
                                    scaleType = ImageView.ScaleType.FIT_CENTER
                                    visibility = View.GONE       // se muestra al tocar el botón
                                }

                                btnMostrar.setOnClickListener {
                                    if (img.visibility == View.GONE) {
                                        if (fotoRuta.isNotEmpty()) {
                                            val url = BASE_URL_IMG + fotoRuta

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
                                            Toast.makeText(
                                                requireContext(),
                                                "Este empleado no tiene foto",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        img.visibility = View.GONE
                                        btnMostrar.text = "Mostrar imagen"
                                    }
                                }

                                contenedor.addView(info)
                                contenedor.addView(btnMostrar)
                                contenedor.addView(img)

                                layoutEmpleados.addView(contenedor)
                            } else {
                                // Por si el backend manda algo raro
                                val errorTxt = TextView(requireContext()).apply {
                                    text = "Formato incorrecto: $item"
                                }
                                layoutEmpleados.addView(errorTxt)
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // ============================
    //        MENÚ OPCIONES
    // ============================
    private fun mostrarMenuOpciones() {
        val bottomSheet = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        )

        val view = layoutInflater.inflate(R.layout.opcionempleado, null)
        bottomSheet.setContentView(view)

        view.findViewById<android.widget.LinearLayout>(R.id.opregistraremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoregistrar())
                .addToBackStack(null).commit()
        }

        view.findViewById<android.widget.LinearLayout>(R.id.opactualizaremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoactualizar())
                .addToBackStack(null).commit()
        }

        view.findViewById<android.widget.LinearLayout>(R.id.opEliminaremp).setOnClickListener {
            bottomSheet.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, empleadoeliminar())
                .addToBackStack(null).commit()
        }

        bottomSheet.show()
    }
}
