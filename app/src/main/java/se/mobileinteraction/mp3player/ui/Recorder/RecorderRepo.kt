package se.mobileinteraction.mp3player.ui.Recorder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.IOException
import java.util.*

class RecorderRepo(val context: Context) {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private val dir = File(context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/")


    internal var recordingTime: Long = 0
    private var timer = Timer()
    private val recordingTimeString = MutableLiveData<String>()

    init {
        try {
            // create a File object for the parent directory
            val recorderDirectory = File(context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/")
            // have the object build the directory structure, if needed.
            recorderDirectory.mkdirs()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (dir.exists()) {
            val count : Int = dir.listFiles()!!.size
            output = context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/recording" + count + ".mp3"
        }

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder?.setAudioChannels(2)

        mediaRecorder?.setOutputFile(output)

    }


    @SuppressLint("RestrictedApi")
    fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            timer = Timer()
            startTimer()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        stopTimer()
        resetTimer()
        initRecorder()

    }


    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    fun pauseRecording() {
        stopTimer()
            mediaRecorder?.pause()


    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    fun resumeRecording() {
        timer = Timer()
        startTimer()
        mediaRecorder?.resume()
    }

    private fun initRecorder() {
        mediaRecorder = MediaRecorder()
        if (dir.exists()) {
            val count : Int = dir.listFiles()!!.size
            output = context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/recording" + count + ".mp3"
        }
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder?.setOutputFile(output)
    }
    private fun startTimer(){
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                recordingTime += 1
                updateDisplay()
            }
        }, 1000, 1000)
    }

    private fun stopTimer(){
        timer.cancel()
    }


    private fun resetTimer() {
        timer.cancel()
        recordingTime = 0
        recordingTimeString.postValue("00:00")
    }

    internal fun updateDisplay(){
        val minutes = recordingTime / (60)
        val seconds = recordingTime % 60
        val str = String.format("%02d:%02d", minutes, seconds)

        recordingTimeString.postValue(str)
    }

    fun getRecordingTime() = recordingTimeString
}