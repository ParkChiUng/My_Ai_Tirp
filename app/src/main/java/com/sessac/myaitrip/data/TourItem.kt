package com.sessac.myaitrip.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tour_item")
data class TourItem(
    @PrimaryKey
    @SerializedName("contentid")
    val contentId: String,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("addr1")
    val address: String? = null,
    @SerializedName("addr2")
    val address2: String? = null,
    @SerializedName("areacode")
    val areaCode: String? = null,
    @SerializedName("booktour")
    val bookTour: String? = null,
    @SerializedName("cat1")
    val category1: String? = null,
    @SerializedName("cat2")
    val category2: String? = null,
    @SerializedName("cat3")
    val category3: String? = null,
    @SerializedName("contenttypeid")
    val contentTypeId: String? = null,
    @SerializedName("createdtime")
    val createdTime: String? = null,
    @SerializedName("firstimage")
    val firstImage: String? = null,
    @SerializedName("firstimage2")
    val firstImage2: String? = null,
    @SerializedName("cpyrhtDivCd")
    val copyrightDivCode: String? = null,
    @SerializedName("mapx")
    val mapX: String? = null,
    @SerializedName("mapy")
    val mapY: String? = null,
    @SerializedName("mlevel")
    val mapLevel: String? = null,
    @SerializedName("modifiedtime")
    val modifiedTime: String? = null,
    @SerializedName("sigungucode")
    val siGunGuCode: String? = null,
    @SerializedName("tel")
    val telephone: String? = null,
    @SerializedName("zipcode")
    val zipCode: String? = null
)