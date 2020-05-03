package se.mobileinteraction.mp3player.ui.SeekBar


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import se.mobileinteraction.mp3player.R
import kotlinx.android.synthetic.main.details_view.view.*
import kotlinx.android.synthetic.main.fragment_player.view.*


class DetailsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.details_view, this, true)

        close_view.setOnClickListener {
            detail_view.isVisible = false
        }


    }

    var isPagingEnabled = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(ev)
    }


}




