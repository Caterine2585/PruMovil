package com.example.mimovil


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mimovil.Acciones.Cliente.clienteFragment
import com.example.mimovil.Acciones.Empleado.empleadoFragment
import com.example.mimovil.Acciones.Proveedor.proveedorFragment
import com.example.mimovil.Acciones.Compras.ComprasFragment
import com.example.mimovil.Acciones.Ventas.ventasFragment
import com.example.mimovil.Acciones.Devoluciones.DevolucionFragment
import com.example.mimovil.Acciones.Producto.ProductoFragment


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
        val cardDevolucion = view.findViewById<LinearLayout>(R.id.cardDevolucion)   // ← SIN TILDE

        configurarCard(cardProductos, ProductoFragment())
        configurarCard(cardCompra, ComprasFragment())
        configurarCard(cardCliente, clienteFragment())
        configurarCard(cardEmpleado, empleadoFragment())
        configurarCard(cardVentas, ventasFragment())
        configurarCard(cardProveedor, proveedorFragment())
        configurarCard(cardDevolucion, DevolucionFragment()) // ← NOMBRE CORRECTO


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
