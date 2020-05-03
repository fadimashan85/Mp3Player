package se.mobileinteraction.mp3player.util

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import se.mobileinteraction.mp3player.R
import com.google.android.material.bottomnavigation.BottomNavigationView


interface Reselectable {
    fun onReselected()
}

object NavExtensions {
    fun AppCompatActivity.setUpBottomNavigation(
        bottomNavigationView: BottomNavigationView,
        navController: NavController
    ) {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (navController.currentDestination?.id != it.itemId) {
                navController.navigate(it.itemId)
                true
            } else {
                false
            }

        }

        bottomNavigationView.setOnNavigationItemReselectedListener {
            val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            val fragment = navHost?.childFragmentManager?.fragments?.get(0)

            if (fragment is Reselectable) {
                fragment.onReselected()
            }
        }
    }


}
