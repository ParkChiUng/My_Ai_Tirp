package com.sessac.myaitrip.data.entities

import com.naver.maps.geometry.LatLng
import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

data class TourClusterItem(var position: LatLng): TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(position.latitude, position.longitude)
    }
    /*
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
     */

    var contentId: String? = null
    var title: String? = null
    var address: String? = null
    var subAddress: String? = null
    var contentTypeId: String? = null
    var distance: String? = null
    var imageUrl: String? = null
    var subImageUrl: String? = null

    constructor(lat: Double, lng: Double): this(LatLng(lat, lng)) {
        contentId = null
        title = null
        address = null
        subAddress = null
        contentTypeId = null
        distance = null
        imageUrl = null
        subImageUrl = null
    }

    constructor(lat: Double, lng: Double, contentId: String, title: String, address: String, subAddress: String, contentTypeId: String, distance: String, imageUrl: String, subImageUrl: String ): this(LatLng(lat, lng)) {
        this.contentId = contentId
        this.title = title
        this.address = address
        this.subAddress = subAddress
        this.contentTypeId = contentTypeId
        this.distance = distance
        this.imageUrl = imageUrl
        this.subImageUrl = subImageUrl
    }

}
