package se.mobileinteraction.mp3player.ui.login

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import se.mobileinteraction.mp3player.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var provider: List<AuthUI.IdpConfig>
    private val MY_REQUEST_CODE : Int = 7717

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        provider = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        showSignInOpitons()

        btn_sign_out.setOnClickListener {
            context?.let { it1 ->
                AuthUI.getInstance().signOut(it1)
                    .addOnCompleteListener{
                        btn_sign_out.isEnabled = false
                        showSignInOpitons()
                    }
                    .addOnFailureListener { e-> Toast.makeText(context,""+ e.message, Toast.LENGTH_SHORT).show()

                    }
            }
        }
        
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MY_REQUEST_CODE){

            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(context,""+ user!!.email, Toast.LENGTH_SHORT).show()
                btn_sign_out.isEnabled = true
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToWavRecorderFragment())
            }else {

                Toast.makeText(context,""+ response!!.error!!.message, Toast.LENGTH_SHORT).show()

            }

        }
    }

    private fun showSignInOpitons() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTheme(R.style.MyTheme)
                .build(), MY_REQUEST_CODE

        )
    }
}
