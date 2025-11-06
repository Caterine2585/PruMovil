package com.example.mimovil

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mimovil.model.EmpleadosFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class SubscriptionFragment : Fragment(R.layout.fragment_subscription)

class MainActivity : AppCompatActivity() {

    private var fab: FloatingActionButton? = null
    private var drawerLayout: DrawerLayout? = null
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        // Menú lateral
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
                R.id.nav_compras   -> replaceFragment(ComprasFragment())
                R.id.nav_productos -> replaceFragment(ProductoFragment()) // 🆕 NUEVA OPCIÓN DE PRODUCTOS
                R.id.nav_share     -> replaceFragment(SubscriptionFragment())
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

        // FAB para mostrar el diálogo inferior
        fab?.setOnClickListener { showBottomDialog() }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val videoLayout = dialog.findViewById<LinearLayout>(R.id.layoutVideo)
        val shortsLayout = dialog.findViewById<LinearLayout>(R.id.layoutShorts)
        val liveLayout = dialog.findViewById<LinearLayout>(R.id.layoutLive)
        val cancelButton = dialog.findViewById<android.widget.ImageView>(R.id.cancelButton)

        videoLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Opción Video seleccionada", Toast.LENGTH_SHORT).show()
        }
        shortsLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Opción Shorts seleccionada", Toast.LENGTH_SHORT).show()
        }
        liveLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Opción Live seleccionada", Toast.LENGTH_SHORT).show()
        }
        cancelButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }
}
