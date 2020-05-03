package se.mobileinteraction.mp3player.ui.Recorder

import androidx.lifecycle.ViewModel
import se.mobileinteraction.mp3player.util.RecorderState


class RecorderViewModel(val repo: RecorderRepo) : ViewModel() {

    var recorderState: RecorderState = RecorderState.Stopped

    fun startRecording() = repo.startRecording()

    fun stopRecording() = repo.stopRecording()

    fun pauseRecording() = repo.pauseRecording()

    fun resumeRecording() = repo.resumeRecording()

    fun getRecordingTime() = repo.getRecordingTime()
}

