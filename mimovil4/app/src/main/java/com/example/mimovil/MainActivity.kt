package com.example.mimovil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mimovil.Acciones.Cliente.clienteFragment
import com.example.mimovil.Acciones.Compras.ComprasFragment
import com.example.mimovil.Acciones.Devoluciones.DevolucionFragment
import com.example.mimovil.Acciones.Empleado.empleadoFragment
import com.example.mimovil.Acciones.Producto.ProductoFragment
import com.example.mimovil.Acciones.Proveedor.proveedorFragment
import com.example.mimovil.Acciones.Ventas.ventasFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // icono hamburguesa
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        // Opciones del menú lateral
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home        -> replaceFragment(HomeFragment())
                R.id.nav_cliente     -> replaceFragment(clienteFragment())
                R.id.nav_empleados   -> replaceFragment(empleadoFragment())
                R.id.nav_ventas      -> replaceFragment(ventasFragment())
                R.id.nav_compras     -> replaceFragment(ComprasFragment())
                R.id.nav_proveedores -> replaceFragment(proveedorFragment())
                R.id.nav_productos   -> replaceFragment(ProductoFragment())
                R.id.nav_devoluciones-> replaceFragment(DevolucionFragment())
                R.id.nav_logout      -> {
                    cerrarSesion()
                }
                else -> Toast.makeText(this, "Opción desconocida", Toast.LENGTH_SHORT).show()
            }
            item.isChecked = true
            drawerLayout?.closeDrawers()
            true
        }

        // Fragment inicial
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    // 🔹 CERRAR SESIÓN REAL
    private fun cerrarSesion() {
        // 1) Borrar los datos del usuario guardados en SharedPreferences
        val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // 2) Ir al LoginActivity limpiando el back stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // 3) Cerrar esta Activity
        finish()
    }
}
