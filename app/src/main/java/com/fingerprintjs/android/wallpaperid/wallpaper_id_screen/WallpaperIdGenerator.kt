package com.fingerprintjs.android.wallpaperid.wallpaper_id_screen


import android.app.WallpaperColors
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.LinkedList
import java.util.concurrent.Executors


interface WallpaperIdGenerator {
    fun getId(listener: (String) -> (Unit))
}

class WallpaperIdGeneratorImpl(
    private val wallpaperInfoProvider: WallpaperInfoProvider
) : WallpaperIdGenerator {

    private val hasher = Sha256Hasher()
    private val executor = Executors.newSingleThreadExecutor()

    override fun getId(listener: (String) -> (Unit)) {
        executor.execute {
            listener.invoke(
                hasher.hash(
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
                        calculateWallpaperBytes()
                    } else calculateColorsBytes()
                )
            )
        }
    }

    private fun calculateWallpaperBytes(): ByteArray {
        val imageBitmap = wallpaperInfoProvider.getWallpaperBitmap() ?: return ByteArray(0)
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun calculateColorsBytes(): ByteArray {
        val colorComponents = LinkedList<Int>()

        colorComponents.addAll(
            extractColors(wallpaperInfoProvider.getSystemWallpaperColors())
        )
        colorComponents.addAll(
            extractColors(wallpaperInfoProvider.getLockscreenWallpaperColors())
        )

        return colorComponents.map {
            ByteArray(4) { index ->
                (it shr index * 8).toByte()
            }
        }.flatMap {
            it.asList()
        }.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun extractColors(wallpaperColors: WallpaperColors?): List<Int> {
        val colors = wallpaperColors ?: return emptyList()
        val primaryColor = colors.primaryColor
        return listOf(
            primaryColor.toArgb(),
            (colors.secondaryColor ?: primaryColor).toArgb(),
            (colors.tertiaryColor ?: primaryColor).toArgb()
        )
    }
}

private class Sha256Hasher() {
    fun hash(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
