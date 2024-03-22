package com.sessac.myaitrip.data.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TourImageItem(
    @SerializedName("contentid")
    val contentId: String,
    @SerializedName("originimgurl")
    val originImgUrl: String? = null,
    @SerializedName("imgname")
    val imgName: String? = null,
    @SerializedName("smallimageurl")
    val smallImageurl: String? = null,
    @SerializedName("cpyrhtDivCd")
    val cpyRhtDivCd: String? = null,
    @SerializedName("serialnum")
    val serialNum: String? = null,
): Parcelable