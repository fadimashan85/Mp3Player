package se.mobileinteraction.mp3player.entities


data class Track(
    var fileName: String ="1",
    var title: String ="1",
    var album: String ="1",
    var path: String ="1",
    var artist: String ="1"
//    var coverPhoto: ImageView ="1",
//    var artwork: Artwork?

) : Comparable<Track> {
    override fun compareTo(other: Track): Int = title!!.compareTo(other.title!!)
    override fun equals(other: Any?): Boolean {
        return (other as? Track)?.title == title
    }
}