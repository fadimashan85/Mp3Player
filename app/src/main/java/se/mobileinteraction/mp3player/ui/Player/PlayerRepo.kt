package se.mobileinteraction.mp3player.ui.Player

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import se.mobileinteraction.mp3player.entities.Track
import java.io.File


class PlayerRepo(val context: Context, val mediaPlayer: MediaPlayer) {

    private val path: String by lazy { context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/" }

    private val _currentSong: MutableLiveData<Track> = MutableLiveData()
    val currentSong: LiveData<Track> = _currentSong

    private val _totalTime: MutableLiveData<Int> = MutableLiveData()
    val totalTime: LiveData<Int> = _totalTime


    fun playMedia(track: Track) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, File("$path${track.fileName}").toUri())
        mediaPlayer.prepare()
        mediaPlayer.start()

        _currentSong.postValue(track)
    }


    fun pauseMedia(track: Track) {
        if (mediaPlayer.isPlaying)
        mediaPlayer.pause()
        _currentSong.postValue(track)
    }
}