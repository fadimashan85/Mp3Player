package se.mobileinteraction.mp3player.ui.Storage

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import se.mobileinteraction.mp3player.R
import se.mobileinteraction.mp3player.entities.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.extensions.showKeyboard
import com.simplemobiletools.commons.extensions.value
import kotlinx.android.synthetic.main.change_name_layout.*
import kotlinx.android.synthetic.main.change_name_layout.view.*
import kotlinx.android.synthetic.main.storage_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class StorageFragment : Fragment(R.layout.storage_layout) {

    val ERROR_BUCKET_NOT_FOUND: Int = -13011
    val ERROR_NOT_AUTHENTICATED: Int = -13020
    val ERROR_OBJECT_NOT_FOUND: Int = -13010
    private val viewModel by viewModel<StorageViewModel>()
    private val storageAdapter = StorageAdapter { track, view ->
        openOption(track, view)

    }

    //        , {track ->
//        playStream(track)
//        })
    var listOfTracks: List<StorageReference>? = null
    var track: Track? = null

    //    val localFile: File by lazy { File.createTempFile("record","wav",(File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/"))) }
    val mediaPlayer = MediaPlayer().apply {
        AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permission()
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef: StorageReference =
                FirebaseStorage.getInstance().getReference("$user/soundrecorder/")

        storageAdapter.onStream = {it ->
            it.downloadUrl.addOnSuccessListener {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(it.toString())
                mediaPlayer.prepare()
                mediaPlayer.start()

            }
        }

        storageAdapter.onPause = {
            mediaPlayer.pause()
        }
        storageRef
                .listAll()
                .addOnCompleteListener {
                        it.result?.items?.map {
                            storageRef.child(it.name)
                            //                    Log.e("Wtf", "${it.name}")
                        }?.let { it1 ->
                            storageAdapter.setData(it1)
                            Log.d("nononononono", "$it1")
                        }

                }

        re_view.layoutManager = LinearLayoutManager(context)
        re_view.adapter = storageAdapter

    }

    fun openOption(track: StorageReference, view: View) {
        val popupWindow = PopupMenu(context, view)
        popupWindow.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.download_btn -> {
                    val storage = FirebaseStorage.getInstance("gs://mp3player-802c5.appspot.com")
                    val localFile: File by lazy {
                        File.createTempFile(
                                "record",
                                ".wav",
                                (File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/"))
                        )
                    }

                    var recordingRef = track
                    var path = requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/"
                    val oldFile = File(path, localFile.name)
                    val oldName = localFile.name
                    val view: View =
                        LayoutInflater.from(requireContext()).inflate(R.layout.change_name_layout, null)
                    AlertDialog.Builder(requireContext())
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", null)
                        .create().apply {
                            requireActivity().setupDialogStuff(view, this, R.string.change_name)
                            showKeyboard(file_name)
                            file_name.hint = oldName
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                                val newFileName = view.file_name.value
                                var newFile = File(path, "${newFileName}.wav")
                                oldFile.renameTo(newFile)

                                if (newFileName.isEmpty()  || newFileName == "") {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please fill a new name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@setOnClickListener
                                }
                               Toast.makeText(context, "${newFile.name} Saved! ", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        }
                    recordingRef.getFile(localFile).addOnSuccessListener {

                        Toast.makeText(context, "Chose a new name ", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.delete_item -> {
                    val storage = FirebaseStorage.getInstance("gs://mp3player-802c5.appspot.com")
                    val localFile: File by lazy {
                        File.createTempFile(
                                "record",
                                ".wav",
                                (File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/"))
                        )
                    }

                    var recordingRef = track
                    recordingRef.delete().addOnSuccessListener {
                        storageAdapter.removeData(recordingRef)
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }

                    true
                }
                else -> false
            }
        }
        popupWindow.inflate(R.menu.item_option_download)
        popupWindow.show()
    }

    private fun permission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, 0)
        }
    }


    // Stream music from firebase storage

//    fun playStream(track: StorageReference){
//        val storage = FirebaseStorage.getInstance("gs://mp3player-802c5.appspot.com")
//        val localFile: File by lazy {
//            File.createTempFile(
//                    "record",
//                    ".wav",
//                    (File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/"))
//            )
//        }
//
//        var recordingRef = track
////        var ref = storage.getReferenceFromUrl(track.toString())
//        var ref = storage.reference.child(track.toString())
//
//        mediaPlayer.setDataSource(ref.toString())
//        mediaPlayer.setOnPreparedListener {
//            it.start()
//        }
//        mediaPlayer.prepareAsync()
//
//    }


}