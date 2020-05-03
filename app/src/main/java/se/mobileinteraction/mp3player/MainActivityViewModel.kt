package se.mobileinteraction.mp3player

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.mobileinteraction.mp3player.R

class MainActivityViewModel(
    val context: Context
) : ViewModel() {

    private val _showTools: MutableLiveData<Int> = MutableLiveData()
    val showTools: LiveData<Int> = _showTools

    private val _showSighnout: MutableLiveData<Int> = MutableLiveData()
    val showSignOut: LiveData<Int> = _showSighnout

    fun showTools(current_id: Int) {
        _showTools.postValue(if (current_id == R.id.loginFragment || current_id == R.id.splashFragment) View.GONE else View.VISIBLE)
    }

    fun showSignOut(current_id: Int){
        _showSighnout.postValue(if (current_id == R.id.wavRecorderFragment) View.VISIBLE else View.GONE)
    }
}