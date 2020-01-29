package com.example.mp3player

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    private val _showBottomBar: MutableLiveData<Int> = MutableLiveData()
    val showBottomBar:LiveData<Int> = _showBottomBar


    fun showBottomBar(current_id: Int){
        _showBottomBar.postValue(if(current_id == R.id.loginFragment) View.GONE else View.VISIBLE)
    }

}