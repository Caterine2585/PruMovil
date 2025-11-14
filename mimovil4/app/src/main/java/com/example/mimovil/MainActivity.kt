package com.example.mimovil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mimovil.model.EmpleadosFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.example.mimovil.model.HomeFragment
class MainActivity : AppCompatActivity() {

    private var fab: FloatingActionButton? = null
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
                R.id.nav_home      -> replaceFragment(HomeFragment())
                R.id.nav_cliente   -> replaceFragment(ClienteFragment())
                R.id.nav_empleados -> replaceFragment(EmpleadosFragment())
                R.id.nav_ventas -> replaceFragment(VentasFragment())
                R.id.nav_compras   -> replaceFragment(ComprasFragment())
                R.id.nav_productos -> replaceFragment(ProductoFragment()) // 🆕 NUEVA OPCIÓN DE PRODUCTOS
                R.id.nav_logout    -> Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                else               -> Toast.makeText(this, "Opción desconocida", Toast.LENGTH_SHORT).show()
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


}
