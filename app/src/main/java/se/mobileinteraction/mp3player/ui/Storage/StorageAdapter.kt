package se.mobileinteraction.mp3player.ui.Storage


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.mobileinteraction.mp3player.R
import com.google.firebase.storage.StorageReference

class StorageAdapter(val onOption: (StorageReference, View) -> Unit) :
//                     , val  onPlay: (StorageReference)-> Unit) :
    RecyclerView.Adapter<StorageAdapter.StorageViewHolder>() {
    private var list = mutableListOf<StorageReference>()
    var onStream: ((StorageReference) -> Unit)? = null
    var onPause: ((StorageReference) -> Unit)? = null

    var playPosition: Int = -1
    var previousPlayPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StorageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.track_item, parent, false
            )
        )

    fun setData(listOfTracks: List<StorageReference>) {
        list.clear()
        list.addAll(listOfTracks)

        notifyDataSetChanged()
    }

    fun removeData(track: StorageReference) {
        list.remove(track)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: StorageViewHolder, position: Int) {
        val item = list[position]
        val isPlaying: Boolean = position == playPosition

        holder.pause.visibility = if (isPlaying) View.VISIBLE else View.GONE
        holder.stream.visibility = if (isPlaying) View.GONE else View.VISIBLE

        holder.itemView.isActivated = isPlaying
        if (isPlaying) previousPlayPosition = position


        holder.bind(item, onOption)

        holder.stream.setOnClickListener {
            playPosition = if (isPlaying) -1 else position
            notifyItemChanged(previousPlayPosition)
            notifyItemChanged(position)
            onStream?.invoke(item)
        }

        holder.pause.setOnClickListener {
            playPosition = if (isPlaying) -1 else position
            notifyItemChanged(previousPlayPosition)
            notifyItemChanged(position)
            onPause?.invoke(item)
        }


    }

    class StorageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = itemView.findViewById<TextView>(R.id.item_name)
        val popupMenu = itemView.findViewById<ImageView>(R.id.popup_menu)
        val stream = itemView.findViewById<ImageView>(R.id.stream)
        val pause = itemView.findViewById<ImageView>(R.id.stop_stream)

        fun bind(track: StorageReference, onOption: (StorageReference, View) -> Unit) {
//                 , onPlay: (StorageReference)-> Unit) {
            title.text = track.name
            popupMenu.setOnClickListener {
                onOption(track, popupMenu)
            }

//            stream.setOnClickListener {
//                onPlay(track)
//            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}
