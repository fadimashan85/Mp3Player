package com.example.mp3player.ui.login


import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mp3player.R
import com.example.mp3player.utils.ScreenUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_splash.*


class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onResume() {
        super.onResume()

        img_logo.apply {
            translationY = ScreenUtils.dipsToPixel(120f)
            animate().alpha(1f).setStartDelay(500).setDuration(300).start()
            animate()
                .setInterpolator(DecelerateInterpolator())
                .translationY(0f)
                .setStartDelay(1000)
                .setDuration(800)
                .withEndAction {
                    navigateToDetails()
                }.start()
        }
    }

    private fun navigateToDetails() {
        if (FirebaseAuth.getInstance().currentUser != null)
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToNavigationHome())
        else findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
    }
}
