package se.mobileinteraction.mp3player.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import se.mobileinteraction.mp3player.R

object ScreenUtils {

    fun getScreenHeight(): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        return displayMetrics.heightPixels / displayMetrics.density
    }

    fun getScreenWidth(): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        return displayMetrics.widthPixels / displayMetrics.density
    }

    fun getActionBarHeight(context: Context): Int {
        val attrs = intArrayOf(R.attr.actionBarSize)
        val actionBarHeight: Int
        context.obtainStyledAttributes(attrs).apply {
            actionBarHeight = getDimensionPixelSize(0, -1)
            recycle()
        }

        return actionBarHeight
    }

    fun getStatusBarHeight() = getDimensions("status_bar_height")

    fun getNavigationBarHeight() = getDimensions("navigation_bar_height")

    private fun getDimensions(identifier: String): Int {
        var result = 0
        val resourceId = Resources.getSystem().getIdentifier(identifier, "dimen", "android")
        if (resourceId > 0) {
            result = Resources.getSystem().getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun dipsToPixel(dips: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, Resources.getSystem().displayMetrics)
}
