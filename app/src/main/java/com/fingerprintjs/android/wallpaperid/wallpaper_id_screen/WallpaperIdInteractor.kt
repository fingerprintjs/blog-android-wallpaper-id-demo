package com.fingerprintjs.android.wallpaperid.wallpaper_id_screen


import com.fingerprintjs.android.wallpaperid.API_ENDPOINT_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface WallpaperIdInteractor {
    fun getWallpaperUniquinessInfo(
        visitorId: String,
        wallpaperHash: String,
        listener: (UniquenessResponse) -> (Unit)
    )
}

class WallpaperIdInteractorImpl: WallpaperIdInteractor {

    private var retrofit: Retrofit? = null

    override fun getWallpaperUniquinessInfo(
        visitorId: String,
        wallpaperHash: String,
        listener: (UniquenessResponse) -> Unit
    ) {
        val apiInterface = getClient()?.create(WallpaperIdApi::class.java)
        val requestBody = UniquenessRequest(visitorId, wallpaperHash)
        val request = apiInterface?.getUniquenessInfo(requestBody) ?: return

        request.enqueue(object : Callback<UniquenessResponse> {
            override fun onResponse(
                call: Call<UniquenessResponse>,
                response: Response<UniquenessResponse>
            ) {
                handleResponse(response, listener)
            }

            override fun onFailure(call: Call<UniquenessResponse>, t: Throwable) {}
        })
    }

    private fun handleResponse(response: Response<UniquenessResponse>, listener: (UniquenessResponse) -> Unit) {
        response.body()?.let {
            listener.invoke(it)
        }
    }

    private fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(API_ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

}