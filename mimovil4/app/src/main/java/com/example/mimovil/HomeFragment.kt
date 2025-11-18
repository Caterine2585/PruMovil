package com.example.mimovil.model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mimovil.ClienteFragment
import com.example.mimovil.ComprasFragment
import com.example.mimovil.DevolucionesFragment
import com.example.mimovil.ProductoFragment
import com.example.mimovil.ProveedorFragment
import com.example.mimovil.R
import com.example.mimovil.VentasFragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardProductos = view.findViewById<LinearLayout>(R.id.cardProductos)
        val cardCompra = view.findViewById<LinearLayout>(R.id.cardCompra)
        val cardCliente = view.findViewById<LinearLayout>(R.id.cardCliente)
        val cardEmpleado = view.findViewById<LinearLayout>(R.id.cardEmpleado)
        val cardVentas = view.findViewById<LinearLayout>(R.id.cardVenta)
        val cardProveedor = view.findViewById<LinearLayout>(R.id.cardProveedor)
        val cardDevolución = view.findViewById<LinearLayout>(R.id.cardDevolución)

        configurarCard(cardProductos, ProductoFragment())
        configurarCard(cardCompra, ComprasFragment())
        configurarCard(cardCliente, ClienteFragment())
        configurarCard(cardEmpleado, EmpleadosFragment())
        configurarCard(cardVentas, VentasFragment())
        configurarCard(cardProveedor, ProveedorFragment())
        configurarCard(cardDevolución, DevolucionesFragment())

        return view
    }

    private fun configurarCard(card: LinearLayout, fragment: Fragment) {
        card.setOnClickListener {
            // Cambia el fragmento al presionar la tarjeta
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit()
        }
    }
}
