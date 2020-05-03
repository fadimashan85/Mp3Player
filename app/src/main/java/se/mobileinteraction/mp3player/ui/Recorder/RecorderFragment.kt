package se.mobileinteraction.mp3player.ui.Recorder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import se.mobileinteraction.mp3player.R
import se.mobileinteraction.mp3player.util.RecorderState
import kotlinx.android.synthetic.main.fragment_mp3_recorder.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class RecorderFragment : Fragment(R.layout.fragment_mp3_recorder) {
    private val viewModel by viewModel<RecorderViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNeededPermissions()
        dataObserver()


        button_start_recording.setOnClickListener {
            if (context?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.RECORD_AUDIO
                    )
                } != PackageManager.PERMISSION_GRANTED && context?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED) {

                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                requestPermissions(permissions, 0)
            } else {
                startRecording()
            }
        }

        button_stop_recording.setOnClickListener {
            stopRecording()
        }

        button_pause_recording.setOnClickListener {
            pauseRecording()
        }

        button_resume_recording.setOnClickListener {
            resumeRecording()
        }



        if (viewModel.recorderState == RecorderState.Stopped) {
            button_stop_recording.isEnabled = false
        }
    }

    private fun checkNeededPermissions() {
        println("Requesting permission")
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED) {
            println("Requesting permission")
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ), 0
            )
        }
    }


    private fun dataObserver() {
        viewModel?.getRecordingTime()?.observe(viewLifecycleOwner, Observer {
            textview_recording_time.text = it
        })
    }

    @SuppressLint("RestrictedApi")
    private fun startRecording() {
        viewModel.startRecording()
        Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show()
        button_stop_recording.isEnabled = true

    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        viewModel.stopRecording()
        Toast.makeText(context, "Stop", Toast.LENGTH_SHORT).show()
        button_stop_recording.isEnabled = false
//        button_start_recording.visibility = View.VISIBLE
//        button_pause_recording.visibility = View.INVISIBLE
//        button_resume_recording.visibility = View.INVISIBLE
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun pauseRecording() {
        viewModel.pauseRecording()
        Toast.makeText(context, "Pause", Toast.LENGTH_SHORT).show()
        button_stop_recording.isEnabled = true
//        button_start_recording.visibility = View.INVISIBLE
//        button_pause_recording.visibility = View.INVISIBLE
//        button_resume_recording.visibility = View.VISIBLE
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun resumeRecording() {
        viewModel.resumeRecording()
        Toast.makeText(context, "Resume", Toast.LENGTH_SHORT).show()
        button_stop_recording.isEnabled = true
//        button_start_recording.visibility = View.INVISIBLE
//        button_pause_recording.visibility = View.VISIBLE
//        button_resume_recording.visibility = View.INVISIBLE
    }

}
