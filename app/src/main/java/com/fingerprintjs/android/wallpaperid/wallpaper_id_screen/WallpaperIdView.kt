package com.fingerprintjs.android.wallpaperid.wallpaper_id_screen


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fingerprintjs.android.wallpaperid.R


interface WallpaperIdView {
    fun setId(id: String)
    fun setUniqueness(sameIdCount: Int, totalIdCount: Int)
    fun setSystemWallpaperColors(primary: Color, secondary: Color, tertiary: Color)
    fun setLockScreenWallpaperColors(primary: Color, secondary: Color, tertiary: Color)
    fun setWallpaper(wallpaperBitmap: Bitmap)
    fun hideWallpaper()
    fun hideWallpaperColors()
    fun setOnSourceButtonClickedListener(listener: () -> (Unit))
    fun setOnArticleButtonClickedListener(listener: () -> (Unit))
    fun setOnRefreshListener(listener: () -> Unit)
}

class WallpaperIdViewImpl(
    activity: Activity
) : WallpaperIdView {

    private val idTextView = activity.findViewById<TextView>(R.id.id_text_view)
    private val uniquenessTextView = activity.findViewById<TextView>(R.id.uniqueness_text_view)
    private val progressBar = activity.findViewById<ProgressBar>(R.id.progress_indicator)

    private val lockScreenColorsContainer = activity.findViewById<CardView>(R.id.lock_screen_colors)
    private val lockscreenPrimaryColorView =
        activity.findViewById<FrameLayout>(R.id.lock_screen_primary_color)
    private val lockscreenSecondaryColorView =
        activity.findViewById<FrameLayout>(R.id.lock_screen_secondary_color)
    private val lockscreenTertiaryColorView =
        activity.findViewById<FrameLayout>(R.id.lock_screen_tertiary_color)

    private val systemScreenColors = activity.findViewById<CardView>(R.id.system_screen_colors)
    private val systemScreenPrimaryColorView =
        activity.findViewById<FrameLayout>(R.id.system_screen_primary_color)
    private val systemScreenSecondaryColorView =
        activity.findViewById<FrameLayout>(R.id.system_screen_secondary_color)
    private val systemScreenTertiaryColorView =
        activity.findViewById<FrameLayout>(R.id.system_screen_tertiary_color)

    private val wallpaperImageView = activity.findViewById<ImageView>(R.id.wallpaper_image)
    private val imageViewContainer = activity.findViewById<CardView>(R.id.image_view_container)

    private val sourceButton = activity.findViewById<TextView>(R.id.github_button)
    private val articleButton = activity.findViewById<TextView>(R.id.read_more_button)

    private val swipeToRefreshView = activity.findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)

    init {
        idTextView.hide()
        progressBar.show()
    }

    override fun setId(id: String) {
        progressBar.hide()
        idTextView.show()
        idTextView.text = id
        swipeToRefreshView.isRefreshing = false
    }

    override fun setUniqueness(sameIdCount: Int, totalIdCount: Int) {
        if (sameIdCount == 0) {
            uniquenessTextView.text = uniquenessTextView.context.getString(
                R.string.unique_wallpaper_description,
                totalIdCount
            )
            return
        }
        uniquenessTextView.text = uniquenessTextView.context.getString(
            R.string.not_unique_wallpaper_description,
            sameIdCount,
            totalIdCount
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setSystemWallpaperColors(primary: Color, secondary: Color, tertiary: Color) {
        systemScreenPrimaryColorView.setBackgroundColor(primary.toArgb())
        systemScreenSecondaryColorView.setBackgroundColor(secondary.toArgb())
        systemScreenTertiaryColorView.setBackgroundColor(tertiary.toArgb())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setLockScreenWallpaperColors(primary: Color, secondary: Color, tertiary: Color) {
        lockscreenPrimaryColorView.setBackgroundColor(primary.toArgb())
        lockscreenSecondaryColorView.setBackgroundColor(secondary.toArgb())
        lockscreenTertiaryColorView.setBackgroundColor(tertiary.toArgb())
    }

    override fun setWallpaper(wallpaperBitmap: Bitmap) {
        wallpaperImageView.setImageBitmap(wallpaperBitmap)
    }

    override fun hideWallpaper() {
        imageViewContainer.visibility = View.GONE

        lockScreenColorsContainer.visibility = View.VISIBLE
        systemScreenColors.visibility = View.VISIBLE

    }

    override fun hideWallpaperColors() {
        wallpaperImageView.visibility = View.VISIBLE

        lockScreenColorsContainer.visibility = View.GONE
        systemScreenColors.visibility = View.GONE
    }

    override fun setOnSourceButtonClickedListener(listener: () -> Unit) {
        sourceButton.setOnClickListener { listener.invoke() }
    }

    override fun setOnArticleButtonClickedListener(listener: () -> Unit) {
        articleButton.setOnClickListener { listener.invoke() }
    }

    override fun setOnRefreshListener(listener: () -> Unit) {
        swipeToRefreshView.setOnRefreshListener {
            listener.invoke()
        }
    }
}

private fun View.hide() {
    visibility = View.GONE
}

private fun View.show() {
    visibility = View.VISIBLE
}
