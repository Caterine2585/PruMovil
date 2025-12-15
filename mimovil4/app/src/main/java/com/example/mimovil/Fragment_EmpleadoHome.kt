package com.example.mimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mimovil.Acciones.Cliente.clienteFragment
import com.example.mimovil.Acciones.Ventas.ventasFragment
import com.example.mimovil.Acciones.Devoluciones.DevolucionFragment
import com.example.mimovil.Acciones.Producto.ProductoFragment


class Fragment_EmpleadoHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empleadohome, container, false)

        val cardProductos = view.findViewById<LinearLayout>(R.id.cardProductos)
        val cardCliente = view.findViewById<LinearLayout>(R.id.cardCliente)
        val cardVentas = view.findViewById<LinearLayout>(R.id.cardVenta)
        val cardDevolucion = view.findViewById<LinearLayout>(R.id.cardDevolucion)


        configurarCard(cardProductos, ProductoFragment())
        configurarCard(cardCliente, clienteFragment())
        configurarCard(cardVentas, ventasFragment())
        configurarCard(cardDevolucion, DevolucionFragment())

        return view
    }

    private fun configurarCard(card: LinearLayout, fragment: Fragment) {
        card.setOnClickListener {
            // Cambia el fragmento al presionar la tarjeta1
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit()
        }
    }
}
