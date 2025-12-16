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
import com.example.mimovil.Acciones.Devoluciones.DevolucionFragment
import com.example.mimovil.Acciones.Producto.ProductoFragment
import com.example.mimovil.Acciones.Ventas.ventasFragment
import com.google.android.material.navigation.NavigationView

class MainEmpleadoActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicioempleado_fragment)


        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home        -> replaceFragment(HomeFragment())
                R.id.nav_cliente     -> replaceFragment(clienteFragment())
                R.id.nav_ventas      -> replaceFragment(ventasFragment())
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

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, Fragment_EmpleadoHome())
            .commit()

    }

    private fun cerrarSesion() {

        val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }
}