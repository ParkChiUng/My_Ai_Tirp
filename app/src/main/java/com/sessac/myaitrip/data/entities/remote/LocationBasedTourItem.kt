package com.sessac.myaitrip.data.entities.remote

import com.google.gson.annotations.SerializedName

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
    @SerializedName("firstimage") val firstImageUrl: String,
    @SerializedName("firstimage2") val firstImage2: String,
    @SerializedName("cpyrhtDivCd") val copyrightDivCode: String,
    @SerializedName("mapx") val mapX: String,
    @SerializedName("mapy") val mapY: String,
    @SerializedName("mlevel") val mapLevel: String,
    @SerializedName("modifiedtime") val modifiedTime: String,
    @SerializedName("sigungucode") val siGunGuCode: String,
    @SerializedName("tel") val telephone: String,
    @SerializedName("zipcode") val zipCode: String
)
