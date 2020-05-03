package se.mobileinteraction.mp3player.ui.WavRecorder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import se.mobileinteraction.mp3player.R
import kotlinx.android.synthetic.main.wav_recorder_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WavRecorderFragment : Fragment(R.layout.wav_recorder_fragment) {


    private val viewModel by viewModel<WavRecorderViewModel>()
    private var isRecording = false

    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 77

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataObserver()

        button_stop_rec.visibility = View.INVISIBLE
        button_start_rec.setOnClickListener {
            if (!isRecording) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSIONS_REQUEST_RECORD_AUDIO
                    )

                } else {
                    startRecording()
                    button_start_rec.visibility = View.INVISIBLE
                    button_stop_rec.visibility = View.VISIBLE
                    isRecording = true
                }
            } else {
                stopRecording()
                button_start_rec.visibility = View.VISIBLE
                button_stop_rec.visibility = View.INVISIBLE
                isRecording = false

            }
        }
        button_stop_rec.setOnClickListener {
            button_start_rec.visibility = View.VISIBLE
            button_stop_rec.visibility = View.INVISIBLE
            stopRecording()
            isRecording = false

        }

    }

    private fun stopRecording() {
        viewModel.stopRecording()
    }

    private fun startRecording() {
        viewModel.startRecording()

    }

    private fun dataObserver(){
        viewModel.getWavRecordingTime().observe(viewLifecycleOwner, Observer {
            recording_wavtime.text = it
        })
    }

    override fun onPause() {

        if(isRecording){
            button_start_rec.visibility = View.VISIBLE
            button_stop_rec.visibility = View.INVISIBLE
            stopRecording()
            isRecording = false
        }
        super.onPause()
    }
}