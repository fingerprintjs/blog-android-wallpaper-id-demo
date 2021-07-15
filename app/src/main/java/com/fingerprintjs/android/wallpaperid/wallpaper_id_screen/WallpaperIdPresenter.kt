package com.fingerprintjs.android.wallpaperid.wallpaper_id_screen


import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.fingerprintjs.android.fingerprint.Fingerprinter
import com.fingerprintjs.android.fingerprint.signal_providers.StabilityLevel
import com.fingerprintjs.android.wallpaperid.ARTICLE_PAGE_URL
import com.fingerprintjs.android.wallpaperid.GITHUB_PAGE_URL
import kotlinx.parcelize.Parcelize


interface WallpaperIdPresenter {
    fun update()

    fun attachView(view: WallpaperIdView)
    fun detachView()

    fun onSaveState(): Parcelable

    fun attachRouter(router: WallpaperIdRouter)
    fun detachRouter()
}


@Parcelize
private class ViewState(
    val wallpaperId: String?,
    val uniquenessResponse: UniquenessResponse?
) : Parcelable


class WallpaperIdPresenterImpl(
    private val wallpaperInfoProvider: WallpaperInfoProvider,
    private val fingerprinter: Fingerprinter,
    state: Parcelable?
) : WallpaperIdPresenter {

    private var wallpaperId: String? = (state as? ViewState)?.wallpaperId
    private var uniquenessResponse: UniquenessResponse? = (state as? ViewState)?.uniquenessResponse

    private var wallpaperIdGenerator = WallpaperIdGeneratorImpl(wallpaperInfoProvider)
    private val interactor: WallpaperIdInteractor = WallpaperIdInteractorImpl()

    private var view: WallpaperIdView? = null
    private var router: WallpaperIdRouter? = null

    override fun onSaveState(): Parcelable {
        return ViewState(
            wallpaperId,
            uniquenessResponse
        )
    }

    override fun update() {
        if ((wallpaperId == null) or (uniquenessResponse == null)) {
            wallpaperIdGenerator.getId { wallpaperId ->
                fingerprinter.getDeviceId { deviceId ->
                    getFingerprints(fingerprinter) { fingerprints ->
                        interactor.getWallpaperUniquinessInfo(
                            "${deviceId.deviceId}$DELIMITER$fingerprints",
                            wallpaperId
                        ) { uniquenessResponse ->
                            this.wallpaperId = wallpaperId
                            this.uniquenessResponse = uniquenessResponse
                            updateId(wallpaperId, uniquenessResponse)
                        }
                    }
                }
            }
        } else {
            updateId(wallpaperId!!, uniquenessResponse!!)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            showWallpaper()
        } else {
            showWallpaperColors()
        }
    }

    override fun attachView(view: WallpaperIdView) {
        this.view = view
        subscribeToView()
    }

    private fun getFingerprints(fingerprinter: Fingerprinter, listener: (String) -> (Unit)) {
        val fingerprintSb = StringBuilder()
        fingerprinter.getFingerprint(StabilityLevel.STABLE) { stableFp ->
            fingerprintSb.append(stableFp.fingerprint).append(DELIMITER)
            fingerprinter.getFingerprint(StabilityLevel.OPTIMAL) { optimalFp ->
                fingerprintSb.append(optimalFp.fingerprint).append(DELIMITER)
                fingerprinter.getFingerprint(StabilityLevel.UNIQUE) { uniqueFp ->
                    fingerprintSb.append(uniqueFp.fingerprint)
                    listener.invoke(fingerprintSb.toString())
                }
            }
        }
    }

    private fun subscribeToView() {
        view?.apply {
            setOnArticleButtonClickedListener {
                router?.openLink(ARTICLE_PAGE_URL)
            }
            setOnSourceButtonClickedListener {
                router?.openLink(GITHUB_PAGE_URL)
            }
            setOnRefreshListener {
                router?.refresh()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun showWallpaperColors() {
        view?.apply {
            hideWallpaper()
            wallpaperInfoProvider.getSystemWallpaperColors()?.let {
                setSystemWallpaperColors(
                    it.primaryColor,
                    it.secondaryColor ?: it.primaryColor,
                    it.tertiaryColor ?: it.primaryColor
                )
            }
            wallpaperInfoProvider.getLockscreenWallpaperColors()?.let {
                setLockScreenWallpaperColors(
                    it.primaryColor,
                    it.secondaryColor ?: it.primaryColor,
                    it.tertiaryColor ?: it.primaryColor
                )
            }
        }
    }

    private fun showWallpaper() {
        view?.apply {
            hideWallpaperColors()
            wallpaperInfoProvider.getWallpaperBitmap()?.let { setWallpaper(it) }
        }
    }

    private fun updateId(wallpaperId: String, uniquenessResponse: UniquenessResponse) {
        view?.apply {
            setId(wallpaperId)
            setUniqueness(uniquenessResponse.sameHashesCount, uniquenessResponse.totalHashesCount)
        }
    }

    override fun detachView() {
        view = null
    }

    override fun attachRouter(router: WallpaperIdRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }
}

private const val DELIMITER = ":"
