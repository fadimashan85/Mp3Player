//package com.example.mp3player
//
//import android.content.Context
//import android.media.MediaPlayer
//import com.example.mp3player.entities.Track
//import com.example.mp3player.ui.Player.PlayerAdapter
//import com.example.mp3player.ui.Player.PlayerFragment
//import com.example.mp3player.ui.WavRecorder.WavRecorderRepo
//import io.mockk.MockKAnnotations
//import io.mockk.every
//import io.mockk.impl.annotations.MockK
//import io.mockk.mockk
//import io.mockk.spyk
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.core.IsEqual.equalTo
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//import org.robolectric.util.FragmentTestUtil.startFragment
//
//
//@RunWith(RobolectricTestRunner::class)
//@Config(manifest = Config.NONE)
//class DownloadTest {
//
//
//    @MockK
//    var wavRepo: WavRecorderRepo = mockk {
//        every { startRecording() } returns Unit
//        every { stopRecording() } returns Unit
//    }
//
//    @MockK
//    lateinit var context: Context
//
//    @MockK
//    private val playerFragment = spyk<PlayerFragment>(recordPrivateCalls = true)
//    lateinit var mediaPlayer: MediaPlayer
//    lateinit var track: Track
//    lateinit var track1: Track
//    lateinit var track2: Track
//    private val playerAdapter = mockk<PlayerAdapter>(relaxed = true)
//
//
//
//    @Before
//    fun setUp() {
//        MockKAnnotations.init(this, relaxed = true)
////        wavRepo = WavRecorderRepo(context, mediaPlayer)
//        every { playerFragment getProperty "playerAdapter" } returns playerAdapter
//
//        track = Track()
//        track1 = Track()
//        track2 = Track()
//
//
//    }
//
//
//    @Test
//    fun addNewAudioFile() {
//        val listOfTrack = listOf(track, track1, track2)
//        playerAdapter.setData(listOfTrack)
//        assertThat(playerAdapter.list.size, equalTo(3))
//    }
//
//    @Test
//    fun removeNewAudioFile() {
//        val list = listOf(track,track1,track2)
//        playerAdapter.setData(list)
//        playerAdapter.removeData(2)
//        assertThat(list, equalTo(listOf(track,track1)))
//    }
//
//
////    @Test
////    fun listSize() {
////        val list: List<Track> = listOf(generateTestTrack(1).first())
////       //given
////
////        verify { wavRepo.startRecording() }
////        verify { wavRepo.stopRecording() }
////
////        assertThat(wavRepo.startRecording(), Matchers.notNullValue())
////
////
////    }
//
////    @Test
////    fun removeTrack() {
////        val list: List<Track> = listOf(generateTestTrack(1).first())
////
////        playerAdapter.setData(list)
////        assertThat(playerFragment.sortedListAp?.size, equalTo(1))
//
////        playerAdapter.removeData(0)
////        assertThat(playerFragment.sortedListAp?.size, equalTo(0))
////    }
//
//    private fun generateTestTrack() = Track(
//            "test1",
//            "title",
//            "album",
//            "path",
//            "artist"
//    )
//
//
////    private fun startRecordNewAudioFile() = with(wavRepo){
////        every { startRecording() } returns generateTestTrack
////
////    }
//
//}
