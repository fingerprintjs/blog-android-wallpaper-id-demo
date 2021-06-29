package com.fingerprintjs.android.wallpaperid


import android.app.WallpaperColors
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap


interface WallpaperInfoProvider {
    fun getSystemWallpaperColors(): WallpaperColors?
    fun getLockscreenWallpaperColors(): WallpaperColors?
    fun getWallpaperBitmap(): Bitmap?
}

class WallpaperInfoProviderImpl(
    private val wallpaperManager: WallpaperManager
) : WallpaperInfoProvider {

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun getSystemWallpaperColors() =
        wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun getLockscreenWallpaperColors() =
        wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_LOCK)

    override fun getWallpaperBitmap() = wallpaperManager.drawable.toBitmap()
}
