package com.fingerprintjs.android.wallpaperid


import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.fingerprintjs.android.fingerprint.Configuration
import com.fingerprintjs.android.fingerprint.FingerprinterFactory


class WallpaperIdActivity : AppCompatActivity(), WallpaperIdRouter {
    private lateinit var presenter: WallpaperIdPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_id)
        init(savedInstanceState)
        presenter.attachRouter(this)
        presenter.attachView(
            WallpaperIdViewImpl(
                this
            )
        )
    }

    override fun onStart() {
        super.onStart()
        presenter.update()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachRouter()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(WALLPAPER_ID_PRESENTER_STATE_KEY, presenter.onSaveState())
    }

    private fun init(savedInstanceState: Bundle?) {
        val state: Parcelable? = savedInstanceState?.getParcelable(WALLPAPER_ID_PRESENTER_STATE_KEY)
        val wallpaperInfoProvider = WallpaperInfoProviderImpl(WallpaperManager.getInstance(this))
        val fingerprinter = FingerprinterFactory.getInstance(this, Configuration(3))
        presenter = WallpaperIdPresenterImpl(
            wallpaperInfoProvider,
            fingerprinter,
            state
        )

    }

    override fun openLink(url: String) {
        openLinkInExternalBrowser(url)
    }

    private fun openLinkInExternalBrowser(link: String) {
        val webpage: Uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

private const val WALLPAPER_ID_PRESENTER_STATE_KEY = "wallpaperScreenPresenterKey"
