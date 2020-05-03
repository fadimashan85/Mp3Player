package se.mobileinteraction.mp3player.ui.Player

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.mobileinteraction.mp3player.R
import se.mobileinteraction.mp3player.entities.Track
import com.firebase.ui.auth.AuthUI
import com.google.common.collect.ImmutableSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.extensions.showKeyboard
import com.simplemobiletools.commons.extensions.value
import ealvatag.audio.AudioFile
import ealvatag.audio.AudioFileIO
import ealvatag.audio.AudioHeader
import ealvatag.tag.FieldKey
import ealvatag.tag.NullTag
import ealvatag.tag.Tag
import kotlinx.android.synthetic.main.change_name_layout.*
import kotlinx.android.synthetic.main.change_name_layout.view.*
import kotlinx.android.synthetic.main.details_view.*
import kotlinx.android.synthetic.main.dialog_layout.*
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.item_recording.view.*
import me.tankery.lib.circularseekbar.CircularSeekBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.lang.reflect.InvocationTargetException
import ealvatag.tag.NullTag.INSTANCE as NullTagINSTANCE

class PlayerFragment : Fragment(R.layout.fragment_player) {

    internal val viewModel by viewModel<PlayerViewModel>()

    private val playerAdapter = PlayerAdapter({ track ->
        val currentAudioFile =
            File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/" + track.fileName)
        val audioFile: AudioFile = AudioFileIO.read(currentAudioFile)
        var tag: Tag = audioFile.tag.or(NullTagINSTANCE)
        val oldTitle: String = tag.getValue(FieldKey.TITLE).or("newTitle")
        val oldArtist: String = tag.getValue(FieldKey.ARTIST).or("newArtist")
        val oldAlbumTitle: String = tag.getValue(FieldKey.ALBUM).or("newAlbum")
        runnable = true
        viewModel.playMedia(track)
        song_t.text = oldTitle
        song_alb_n.text = oldAlbumTitle
        song_art_n.text = oldArtist
        song_name.text = track.fileName
        Runnable()
        seekBar.max = viewModel.mediaPlayer.duration.toFloat()
        position_bar.max = viewModel.mediaPlayer.duration
        seekbarChangeListener()
        detail_view.isVisible = true
        play_btn.visibility = View.INVISIBLE
        pause_btn.visibility = View.VISIBLE
    }, { track, view, position ->
        openOptionDevice(track, view, position)
    })

    val recorderDirectory: File by lazy { File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/") }
    var runnable: Boolean = true
    var track: Track? = null
    var pause: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**  callback closing the details view **/
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (detail_view.isVisible) detail_view.visibility = View.GONE
        }

        /** Seekbar setting **/
        seekBar.max = 0f
        seekBar.progress = 0f
        position_bar.max = 0
        position_bar.progress = 0
        initSeekbarChangeListener()


        permission()

        /** Visualizer in details play view  **/
        visualizer.setColor(ContextCompat.getColor(requireContext(), R.color.md_blue_500_dark))
        visualizer.setPlayer(viewModel.repo.mediaPlayer.audioSessionId)


        /** Adapter/Recyclerview  **/
        setItemsFromAdapter()
        recordings_recyclerview.post {
            setItemsFromAdapter()

        }
        recordings_recyclerview.apply {
            setHasFixedSize(true)
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }


        /** Swipe right to share and left to delete **/
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    if (direction == ItemTouchHelper.LEFT) {
                        val builder = AlertDialog.Builder(requireContext())
                        val ttt = viewHolder.itemView.recording_title_textview.value
                        var filePath: File =
                            (File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/${ttt}"))

                        builder.setMessage("Are you sure you want to Delete $ttt?")

                            .setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->
                                filePath.delete()
                                playerAdapter.removeWithSwap(viewHolder)
                                setItemsFromAdapter()
                            }
                            .setNegativeButton("No") { dialog, id ->
                                dialog.dismiss()
                                setItemsFromAdapter()

                            }
                        val alert = builder.create()
                        alert.show()

                    }else  if (direction == ItemTouchHelper.RIGHT) {
                        val ttt = viewHolder.itemView.recording_title_textview.value
                        var audioFile: File =
                            (File(requireActivity().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/$ttt"))
                        val uri = FileProvider.getUriForFile(
                            requireActivity(),
                            "se.mobileinteraction.mp3player.fileprovider",
                            audioFile
                        )
                        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity()).apply {
                            setType("audio/wav")
                            setStream(uri)
                        }.createChooserIntent()
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(shareIntent, "share"))
                        setItemsFromAdapter()

                    }
                }
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    var swipedBackground = ColorDrawable(Color.parseColor("#E691031F"))
                    var swipedBackgroundGreen = ColorDrawable(Color.parseColor("#E61E4902"))

                    val itemView = viewHolder.itemView
                    val deleteIcon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_delete_24
                    )!!
                    val shareIcon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_share_24
                    )!!
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                    if (dX > 0) {
                        swipedBackgroundGreen.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        shareIcon.setBounds(
                            itemView.left + iconMargin,
                            itemView.top + iconMargin,
                            itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMargin
                        )


                    } else {
                        swipedBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                            itemView.top + iconMargin,
                            itemView.right - iconMargin,
                            itemView.bottom - iconMargin
                        )

                    }
                    swipedBackgroundGreen.draw(c)
                    swipedBackground.draw(c)
                    deleteIcon.draw(c)
                    shareIcon.draw(c)
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recordings_recyclerview)

        /** Buttons click with visibility settings **/
        playerAdapter.onPlayN = { it ->
            runnable = true
            viewModel.playMedia(it)
            seekBar.max = viewModel.repo.mediaPlayer.duration.toFloat()
            position_bar.max = viewModel.repo.mediaPlayer.duration
            Runnable()
            play_btn.visibility = View.INVISIBLE
            pause_btn.visibility = View.VISIBLE
        }

        playerAdapter.onPauseN = { it ->
            runnable = false
            viewModel.pauseSong(it)
            Runnable()
            play_btn.visibility = View.VISIBLE
            pause_btn.visibility = View.INVISIBLE
        }

        pause_btn.visibility = View.INVISIBLE

        play_btn.setOnClickListener {
            try {


                if (pause || viewModel.repo.mediaPlayer.isPlaying) {
                    runnable = true
                    viewModel.repo.mediaPlayer.seekTo(viewModel.repo.mediaPlayer.currentPosition)
                    viewModel.repo.mediaPlayer.start()
                    position_bar.max = viewModel.repo.mediaPlayer.duration
                    pause = false
                    Runnable()
                    play_btn.visibility = View.INVISIBLE
                    pause_btn.visibility = View.VISIBLE
                } else {
                    position_bar.progress = 0
                    runnable = true
                    pause = true
                    viewModel.repo.mediaPlayer.reset()
                    viewModel.playMedia(viewModel.currentSong.value!!)
                    position_bar.max = viewModel.repo.mediaPlayer.duration
                    Runnable()
                    play_btn.visibility = View.INVISIBLE
                    pause_btn.visibility = View.VISIBLE
                }
            } catch (e: NullPointerException) {
                Toast.makeText(context, "No record voice was chosen to play", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        pause_btn.setOnClickListener {
            if (viewModel.repo.mediaPlayer.isPlaying)
                runnable = false
            pause = true
            viewModel.repo.mediaPlayer.pause()
            Runnable()
            play_btn.visibility = View.VISIBLE
            pause_btn.visibility = View.INVISIBLE
        }

        btn_sign_out2.setOnClickListener {
            AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener {
                    btn_sign_out2.isEnabled = false
                    findNavController().navigate(PlayerFragmentDirections.actionToLoginFragment())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()

                }
        }
    }


    /** SeekBar with Runnable, handler and timer **/
    fun seekbarChangeListener() {
        seekBar.setOnSeekBarChangeListener(
            object : CircularSeekBar.OnCircularSeekBarChangeListener {
                override fun onProgressChanged(
                    circularSeekBar: CircularSeekBar?,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        play_timer_P.text = (progress / 1000).toString()
                        remain_time_P.text = ((seekBar.max - progress) / 1000).toString()
                        viewModel.repo.mediaPlayer.seekTo(progress.toInt())
                    }
                }

                override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
                }
            })

    }

    private fun initSeekbarChangeListener() {
        position_bar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        remain_time.text = "-${((position_bar.max - progress) / 1000)}"
                        viewModel.mediaPlayer.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
    }

    fun Runnable() {
        Thread(Runnable {
            while (runnable) {
                try {
                    if (viewModel.repo.mediaPlayer.isPlaying) {
                        var msg = Message()
                        msg.what = viewModel.repo.mediaPlayer.currentPosition
                        handler.sendMessage(msg)
                        Thread.sleep(10)
                    }

                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what
            position_bar?.progress = currentPosition
            seekBar?.progress = (currentPosition.toFloat())

            var elapsedTime = createTimeLable(currentPosition)
            play_timer_P?.text = elapsedTime

            var remainingTime =
                createTimeLable(viewModel.repo.mediaPlayer.duration - currentPosition)
            remain_time?.text = "-$remainingTime"
            remain_time_P?.text = "-$remainingTime"
        }
    }

    internal fun createTimeLable(time: Int): String {
        var timeLable = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLable = "$min"
        if (sec < 10) timeLable += ""
        timeLable += "$sec"
        return timeLable
    }


    /** Access permissions **/
    private fun permission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, 0)
        }
    }


    /** Rename audio file dialog **/
    fun changeNameDialog(track: Track) {
        val view: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.change_name_layout, null)
        var path = requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/"
        var oldFile = File(path, track.fileName)
        var oldFileName = track.fileName

        AlertDialog.Builder(requireContext())
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .create().apply {
                requireActivity().setupDialogStuff(view, this, R.string.change_name)
                showKeyboard(file_name)
                file_name.hint = oldFileName

                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val newFileName = view.file_name.value
                    var newFile = File(path, "${newFileName}.wav")
                    oldFile.renameTo(newFile)

                    if (newFileName.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Please fill a new name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    val sortedList =
                        recorderDirectory.listFiles().sortedBy { it.nameWithoutExtension }
                    playerAdapter.setData(sortedList.map { file ->
                        Track(
                            file.name,
                            "oldTitle",
                            "file.album",
                            "defPath",
                            "defArtist"
                        )
                    })
                    dismiss()
                }
            }
    }

    /** Rename ID3tag audio file dialog **/
    fun openDialogNew(cTrack: Track) {
        try {
            val view: View =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout, null)
            val currentAudioFile =
                File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/" + cTrack.fileName)
            val audioFile: AudioFile = AudioFileIO.read(currentAudioFile)
            val audioHeader: AudioHeader = audioFile.audioHeader
            val channels: Int = audioHeader.channelCount
            val bitRate: Int = audioHeader.bitRate
            val encodingType: String = audioHeader.encodingType
            var tag: Tag = audioFile.tag.or(NullTag.INSTANCE)
            val oldTitle: String = tag.getValue(FieldKey.TITLE).or("newTitle")
            val oldArtist: String = tag.getValue(FieldKey.ARTIST).or("newArtist")
            val oldAlbumTitle: String = tag.getValue(FieldKey.ALBUM).or("newAlbum")
            val oldCoverImg = tag.firstArtwork

            AlertDialog.Builder(requireContext())
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create().apply {
                    requireActivity().setupDialogStuff(view, this, R.string.rename_track)
                    showKeyboard(song_title)
                    song_title.hint = oldTitle
                    song_artist.hint = oldArtist
                    album_title.hint = oldAlbumTitle

                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val newTitle = view.song_title.value
                        val newArtist = view.song_artist.value
                        val newAlbum = view.album_title.value

                        if (newTitle.isEmpty() || newAlbum.isEmpty() || newArtist.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please fill all fields",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        track?.title = newTitle
                        track?.artist = newArtist
                        track?.album = newAlbum

                        if ("" == track?.title) {
                            if (tag == NullTagINSTANCE) {
                                tag = audioFile.setNewDefaultTag()
                            }
                        }

                        tag.setField(FieldKey.TITLE, newTitle)
                        tag.setField(FieldKey.ARTIST, newArtist)
                        tag.setField(FieldKey.ALBUM, newAlbum)

                        val supportedFields: ImmutableSet<FieldKey> = tag.supportedFields
                        if (supportedFields.contains(FieldKey.COVER_ART)) {
                            /** here add artwork to the audio file **/
                        }
                        audioFile.save()
                        dismiss()
                    }
                }
        } catch (e: NullPointerException) {
            Toast.makeText(context, "there is no selected track to edit", Toast.LENGTH_SHORT).show()
        }

    }

    /** item button for more options (upload to firebase, delete audio track, rename audio track, change ID3tag and share audio track) **/
    fun openOptionDevice(track: Track, view: View, position: Int) {
        val popupWindow = PopupMenu(context, view)
        popupWindow.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.upload_btn -> {
                    val user = FirebaseAuth.getInstance().currentUser?.uid
                    val storageRef: StorageReference by lazy {
                        FirebaseStorage.getInstance("gs://mp3player-802c5.appspot.com")
                            .getReference("$user/soundrecorder/${track.fileName}")
                    }
                    var filePath: Uri =
                        (File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/${track.fileName}").toUri())
                    storageRef.putFile(filePath).addOnSuccessListener {
                        Toast.makeText(context, "Uploaded!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.delete_local_item -> {
                    val builder = AlertDialog.Builder(requireContext())
                    var filePath: File =
                        (File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/${track.fileName}"))

                    builder.setMessage("Are you sure you want to Delete ${track.fileName}")

                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            playerAdapter.removeData(position)
                            filePath.delete()
                            setItemsFromAdapter()
                        }
                        .setNegativeButton("No") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                            setItemsFromAdapter()

                        }
                    val alert = builder.create()
                    alert.show()



                    true
                }
                R.id.rename_item_name -> {
                    changeNameDialog(track)

                    true
                }
                R.id.id3tag -> {
                    openDialogNew(track)

                    true
                }

                R.id.share -> {
                    var audioFile: File =
                        (File(requireActivity().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/${track.fileName}"))
                    val uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "se.mobileinteraction.mp3player.fileprovider",
                        audioFile
                    )
                    val shareIntent = ShareCompat.IntentBuilder.from(requireActivity()).apply {
                        setType("audio/wav")
                        setStream(uri)
                    }.createChooserIntent()
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(Intent.createChooser(shareIntent, "share"))

                    true
                }
                else -> false
            }
        }
        popupWindow.inflate(R.menu.item_option_upload)
        popupWindow.show()
    }


    /** Adapter items setter   **/
    fun setItemsFromAdapter() {
        val sortedListAp = recorderDirectory.listFiles().sortedBy { it.name }
        try {
            playerAdapter.setData(sortedListAp.map { file ->
                val currentAudioFile =
                    File(requireContext().getExternalFilesDir(null)!!.absolutePath + "/soundrecorder/" + file.name)
                val audioFile: AudioFile = AudioFileIO.read(currentAudioFile)
                var tag: Tag = audioFile.tag.or(NullTagINSTANCE)
                val oldTitle: String = tag.getValue(FieldKey.TITLE).or("newTitle")
                val oldArtist: String = tag.getValue(FieldKey.ARTIST).or("newArtist")
                val oldAlbumTitle: String = tag.getValue(FieldKey.ALBUM).or("newAlbum")
                Track(
                    file.name,
                    "Title: $oldTitle",
                    "Group: $oldAlbumTitle",
                    "defPath",
                    "Voice: $oldArtist"

                )

            })
        } catch (e: Exception) {
            when (e) {
                is NullPointerException,
                is InvocationTargetException ->
                    Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        position_bar.progress = viewModel.repo.mediaPlayer.currentPosition
        runnable = true
        position_bar.max = viewModel.repo.mediaPlayer.duration
        Runnable()
        super.onResume()

    }

}
