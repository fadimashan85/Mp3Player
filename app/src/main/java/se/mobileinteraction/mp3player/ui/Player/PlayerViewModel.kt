package se.mobileinteraction.mp3player.ui.Player

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import se.mobileinteraction.mp3player.entities.Track

class PlayerViewModel(val repo: PlayerRepo) : ViewModel() {

    val currentSong = repo.currentSong
    val mediaPlayer: MediaPlayer = repo.mediaPlayer

    fun playMedia(track: Track) {
        repo.playMedia(track)
    }

    fun pauseSong(track: Track) {
        repo.pauseMedia(track)
    }
}
