package com.example.mp3player

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mp3player.ui.login.LoginFragment
import com.example.mp3player.util.NavExtensions.setUpBottomNavigation
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_bottom)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        btn_sign_out1.isEnabled = true
        setUpBottomNavigation(nav_bottom, navController)
        navView.setupWithNavController(navController)
        observerData()

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.showBottomBar(destination.id)
        }

        btn_sign_out1.setOnClickListener {
            let { it1 ->
                AuthUI.getInstance().signOut(it1)
                    .addOnCompleteListener{
                        btn_sign_out1.isEnabled = false
                        findNavController(R.id.nav_host_fragment).navigate(R.id.action_to_loginFragment)
                    }
                    .addOnFailureListener { e-> Toast.makeText(this,""+ e.message, Toast.LENGTH_SHORT).show()

                    }
            }
        }
    }

    private fun observerData(){
        viewModel.showBottomBar.observe(this, Observer {
            nav_bottom.visibility = it
        })
    }

    override fun onResume() {
        super.onResume()
        btn_sign_out1.isEnabled = true

    }
}
