package se.mobileinteraction.mp3player.ui.WavRecorder

import androidx.lifecycle.ViewModel

class WavRecorderViewModel(val repo: WavRecorderRepo) : ViewModel() {

    fun startRecording() = repo.startRecording()

    fun stopRecording() = repo.stopRecording()

    fun getWavRecordingTime() = repo.getWavRecordingTime()
}
