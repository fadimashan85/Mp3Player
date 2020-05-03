package se.mobileinteraction.mp3player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.mobileinteraction.mp3player.util.NavExtensions.setUpBottomNavigation

const val TAG = "MyFirebaseToken"

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel by viewModel<MainActivityViewModel>()

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().subscribeToTopic("Android_test")

        val navView: BottomNavigationView = findViewById(R.id.nav_bottom)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.play_list_fragment, R.id.wav_recorder
            )
        )

        Log.d("TOKEN", FirebaseInstanceId.getInstance().token)



        btn_sign_out1.isEnabled = true
        setUpBottomNavigation(nav_bottom, navController)
        navView.setupWithNavController(navController)
        observerData()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.showTools(destination.id)
            viewModel.showSignOut(destination.id)
        }

        btn_sign_out1.setOnClickListener {
            let { it1 ->
                AuthUI.getInstance().signOut(it1)
                    .addOnCompleteListener {
                        btn_sign_out1.isEnabled = false
                        findNavController(R.id.nav_host_fragment).navigate(R.id.action_to_loginFragment)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                    }
            }

        }


        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
            })

    }


    private fun observerData() {
        viewModel.showTools.observe(this, Observer {
            nav_bottom.visibility = it
        })
        viewModel.showSignOut.observe(this, Observer {
            btn_sign_out1.visibility = it
        })

    }

    override fun onResume() {
        super.onResume()
        btn_sign_out1.isEnabled = true

    }

    override fun onBackPressed() {
        super.onBackPressed()

    }


}

