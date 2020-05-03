//package se.mobileinteraction.mp3player
//
//import android.app.Application
//import android.media.MediaPlayer
//import se.mobileinteraction.mp3player.entities.Track
//import se.mobileinteraction.mp3player.ui.Player.PlayerAdapter
//import se.mobileinteraction.mp3player.ui.Player.PlayerFragment
//import se.mobileinteraction.mp3player.ui.Player.PlayerRepo
//import se.mobileinteraction.mp3player.ui.Player.PlayerViewModel
//import se.mobileinteraction.mp3player.ui.WavRecorder.WavRecorderRepo
//import io.mockk.MockKAnnotations
//import io.mockk.impl.annotations.MockK
//import androidx.test.core.app.ApplicationProvider
//import com.jraska.livedata.TestObserver.test
//import io.mockk.mockk
//import io.mockk.spyk
//import io.mockk.verify
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.koin.core.context.KoinContext
//
//import org.robolectric.RobolectricTestRunner
//
//
//@RunWith(RobolectricTestRunner::class)
//class DownloadTest : KoinTest {
//
//    var context1: KoinContext = ApplicationProvider.getApplicationContext()
//
//    @MockK
//    private lateinit var context: Application
//
//    @MockK
//    private val mediaPlayer: MediaPlayer = mockk(relaxed = true)
//
//    @MockK
//    var wavRepo: WavRecorderRepo = mockk(relaxed = true)
//    var playRepo: PlayerRepo = PlayerRepo(context, mediaPlayer)
//    private val playerFragment: PlayerFragment = PlayerFragment()
//    private val viewModel: PlayerViewModel = PlayerViewModel(playRepo)
//
//
//    @MockK
//    private val playerFragment1 = spyk<PlayerFragment>(recordPrivateCalls = true)
//    lateinit var track: Track
//    lateinit var track1: Track
//    lateinit var track2: Track
//    private val playerAdapter = mockk<PlayerAdapter>(relaxed = true)
//    lateinit var list1: MutableList<Track>
//
//    @Test
//    fun fetchContext() {
////        startKoin(
////            listOf(context, koinApplication {  })
////        )
//    }
//
//    @Before
//    fun setUp() {
//        MockKAnnotations.init(this, relaxed = true)
//        wavRepo = WavRecorderRepo(context)
//
////        every { playerFragment getProperty "playerAdapter" } returns mockk("playerAdapter")
//
////        every { list1 getProperty "list"} returns playerAdapter.list
////        track = Track()
////        track1 = Track()
////        track2 = Track()
////        list1 = playerAdapter.list
////        every { playerAdapter.list } returns mutableListOf(track, track1, track2)
//
//
//    }
//
//    @Test
//    fun ` lets check if the function was called`() {
//
//        viewModel.playMedia(track)
//
//        verify { wavRepo.startRecording() }
//
//
//    }
////    @Test
////    fun addNewAudioFile() {
////        val listOfTrack = mutableListOf<Track>(track, track1, track2)
////        playerAdapter1.setData(listOfTrack)
////        assertThat(list1, equalTo(mutableListOf<Track>(track, track1, track2)))
////    }
////
////    @Test
////    fun removeNewAudioFile() {
////        val list = listOf<Track>(track,track1,track2)
////        playerAdapter1.setData(list)
////        playerAdapter1.removeData(2)
////        assertThat(list1, equalTo(mutableListOf(track,track1)))
////    }
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
//        "test1",
//        "title",
//        "album",
//        "path",
//        "artist"
//    )
//
//
////    private fun startRecordNewAudioFile() = with(wavRepo){
////        every { startRecording() } returns generateTestTrack
////
////    }
//
//}
