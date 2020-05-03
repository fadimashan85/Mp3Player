package se.mobileinteraction.mp3player.ui.WavRecorder

import android.content.Context
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.github.squti.androidwaverecorder.WaveConfig
import com.github.squti.androidwaverecorder.WaveRecorder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.moshi.internal.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class WavRecorderRepo(val context: Context) {

    private lateinit var audioRecorder: AudioRecord
    private lateinit var waveRecorder: WaveRecorder
    var waveConfig: WaveConfig = WaveConfig()
    private var isRecording = false
    private var isPaused = false
    var filePath: String? = ""
    var onAmplitudeListener: ((Int) -> Util)? = null
    private val wavDir = File(context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/")
    private lateinit var storage: StorageReference

    private var wavTimer = Timer()
    private val wavRecordingTimeString = MutableLiveData<String>()
    internal var wavRecordingTime: Long = 0


    init {

        storage = FirebaseStorage.getInstance().getReference("records")

        try {
            val recorderDirectory =
                File(context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/")
            recorderDirectory.mkdirs()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (wavDir.exists()) {
            val date = DateTimeFormatter.ofPattern("yyyyMMdd-HH-mm-ss").withZone(ZoneOffset.of("+01:00"))
                .format(Instant.now()).drop(5)
            var audioName: String = "rec$date.wav"

            filePath =
                context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/$audioName"
            waveRecorder = WaveRecorder(filePath!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording() {
        if (!isAudioRecorderInitialized()) {
            audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                waveConfig.sampleRate,
                waveConfig.channels,
                waveConfig.audioEncoding,
                AudioRecord.getMinBufferSize(
                    waveConfig.sampleRate,
                    waveConfig.channels,
                    waveConfig.audioEncoding
                )
            )
            val date = DateTimeFormatter.ofPattern("yyyyMMdd-HH-mm-ss").withZone(ZoneOffset.of("+01:00"))
                    .format(Instant.now()).drop(5)
            var audioName: String = "rec$date.wav"


            filePath =
                context.getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/$audioName"
            isRecording = true
            audioRecorder.startRecording()
            startWavTimer()
            GlobalScope.launch(Dispatchers.IO) {
                writeAudioDataToStorage()
            }
        }
        isRecording = true
    }

    fun stopRecording() {
        if (isAudioRecorderInitialized()) {
            isRecording = false
            audioRecorder.stop()
            audioRecorder.release()
            stopWavTimer()
            resetWavTimer()
            WaveHeaderWriter(filePath!!, waveConfig).writeHeader()
        } else {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAudioRecorderInitialized(): Boolean =
        this::audioRecorder.isInitialized && audioRecorder.state == AudioRecord.STATE_INITIALIZED


    private fun calculateAmplitudeMax(data: ByteArray): Int {
        val shortData = ShortArray(data.size / 2)
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
            .get(shortData)

        return shortData.max()?.toInt() ?: 0
    }


    private suspend fun writeAudioDataToStorage() {
        val bufferSize = AudioRecord.getMinBufferSize(
            waveConfig.sampleRate,
            waveConfig.channels,
            waveConfig.audioEncoding
        )
        val data = ByteArray(bufferSize)
        val outputStream = File(filePath).outputStream()
        while (isRecording) {
            val operationStatus = audioRecorder.read(data, 0, bufferSize)

            if (AudioRecord.ERROR_INVALID_OPERATION != operationStatus) {
                if (!isPaused) outputStream.write(data)

                onAmplitudeListener?.let {

                    withContext(Dispatchers.Default) {
                        it(calculateAmplitudeMax(data))
                    }
                }
            }
        }
    }

    private fun startWavTimer() {
        wavTimer = Timer()
        wavTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                wavRecordingTime += 1
                wavUpdateDisplay()
            }
        }, 1000, 1000)
    }

    private fun stopWavTimer() {
        wavTimer.cancel()
    }


    private fun resetWavTimer() {
        wavTimer.cancel()
        wavRecordingTime = 0
        wavRecordingTimeString.postValue("00:00")
    }

    internal fun wavUpdateDisplay() {
        val minutes = wavRecordingTime / (60)
        val seconds = wavRecordingTime % 60
        val str = String.format("%02d:%02d", minutes, seconds)

        wavRecordingTimeString.postValue(str)
    }

    fun getWavRecordingTime() = wavRecordingTimeString

}
