package com.fingerprintjs.android.wallpaperid


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


data class UniquenessRequest(
    @SerializedName("visitorId")
    val visitorId: String,
    @SerializedName("appHash")
    val wallpaperHash: String
)

@Parcelize
data class UniquenessResponse(
    @SerializedName("count")
    val sameHashesCount: Int,
    @SerializedName("totalCount")
    val totalHashesCount: Int
) : Parcelable

interface WallpaperIdApi {
    @POST("app_hashes")
    fun getUniquenessInfo(@Body request: UniquenessRequest): Call<UniquenessResponse>
}