package se.mobileinteraction.mp3player.ui.Player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.mobileinteraction.mp3player.R
import se.mobileinteraction.mp3player.entities.Track


class PlayerAdapter(
    val onItemClick: (Track) -> Unit,
    val onOption: (Track, View, Int) -> Unit
) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    var list = mutableListOf<Track>()
    var expandedPosition: Int = -1
    var previousExpandedPosition: Int = -1


    var onPlayN: ((Track) -> Unit)? = null
    var onPauseN: ((Track) -> Unit)? = null
    var playPosition: Int = -1
    var previousPlayPosition: Int = -1

    var removedItem: Track? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlayerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recording, parent, false
            )
        )

    fun setData(listOfTracks: List<Track>) {
        list.clear()
        list.addAll(listOfTracks)
        notifyDataSetChanged()
    }

    fun removeData(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()

    }

    fun removeWithSwap(viewHolder: RecyclerView.ViewHolder) {

        removedItem = list[viewHolder.adapterPosition]
        list.removeAt(viewHolder.adapterPosition)
        notifyDataSetChanged()


    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val itemPosition = list[position]
        val isExpanded: Boolean = position == expandedPosition
        val isPlaying: Boolean = position == playPosition
        holder.bind(itemPosition, onItemClick, onOption)

        holder.expandView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        if (isExpanded) previousExpandedPosition = position

        holder.pauseView.visibility = if (isPlaying) View.VISIBLE else View.GONE
        holder.playView.visibility = if (isPlaying) View.GONE else View.VISIBLE
        holder.itemView.isActivated = isPlaying
        if (isPlaying) previousPlayPosition = position

        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }

        holder.playView.setOnClickListener {
            playPosition = if (isPlaying) -1 else position
            notifyItemChanged(previousPlayPosition)
            notifyItemChanged(position)
            onPlayN?.invoke(itemPosition)
        }

        holder.pauseView.setOnClickListener {
            playPosition = if (isPlaying) -1 else position
            notifyItemChanged(previousPlayPosition)
            notifyItemChanged(position)
            onPauseN?.invoke(itemPosition)
        }
    }


    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title1 = itemView.findViewById<TextView>(R.id.recording_title_textview)
        var playView = itemView.findViewById<ImageView>(R.id.play_btn1)
        val pauseView = itemView.findViewById<ImageView>(R.id.pause_btn1)
        val popupMore = itemView.findViewById<ImageView>(R.id.more_btn)
        val expandView = itemView.findViewById<View>(R.id.expand_view)
        val artist = itemView.findViewById<TextView>(R.id.artist)
        val titleitem = itemView.findViewById<TextView>(R.id.title)
        val album = itemView.findViewById<TextView>(R.id.album)

        // do ->
        fun bind(
            track: Track,
            onItemClick: (Track) -> Unit,
            onOption: (Track, View, Int) -> Unit
        ) {
            title1.text = track.fileName
            artist.text = track.artist
            titleitem.text = track.title
            album.text = track.album

            itemView.setOnLongClickListener {
                onItemClick(track)
                true
            }

            popupMore.setOnClickListener {

                onOption(track, popupMore, adapterPosition)
            }




        }
    }

    override fun getItemCount() = list.size


}


