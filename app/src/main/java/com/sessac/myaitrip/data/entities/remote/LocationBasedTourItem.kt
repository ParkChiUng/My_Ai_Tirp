package com.sessac.myaitrip.data.entities.remote

import com.google.gson.annotations.SerializedName
import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng
import java.io.Serializable

data class LocationBasedTourItem(
    @SerializedName("contentid") val contentId: String,
    @SerializedName("title") val title: String,
    @SerializedName("addr1") val address: String,
    @SerializedName("addr2") val address2: String,
    @SerializedName("areacode") val areaCode: String,
    @SerializedName("booktour") val bookTour: String,
    @SerializedName("cat1") val category1: String,
    @SerializedName("cat2") val category2: String,
    @SerializedName("cat3") val category3: String,
    @SerializedName("contenttypeid") val contentTypeId: String,
    @SerializedName("createdtime") val createdTime: String,
    @SerializedName("dist") val distance: String,
    @SerializedName("firstimage") val imageUrl: String,
    @SerializedName("firstimage2") val subImageUrl: String,
    @SerializedName("cpyrhtDivCd") val copyrightDivCode: String,
    @SerializedName("mapx") val longitude: String,
    @SerializedName("mapy") val latitude: String,
    @SerializedName("mlevel") val mapLevel: String,
    @SerializedName("modifiedtime") val modifiedTime: String,
    @SerializedName("sigungucode") val siGunGuCode: String,
    @SerializedName("tel") val telephone: String,
    @SerializedName("zipcode") val zipCode: String
): TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(latitude.toDouble(), longitude.toDouble())
    }

}
