package com.sessac.myaitrip.data.entities.remote

import com.google.gson.annotations.SerializedName
import com.sessac.myaitrip.data.entities.TourDetailItem
import com.sessac.myaitrip.data.entities.TourImageItem
import com.sessac.myaitrip.data.entities.TourItem

data class ApiResponse<T>(
    @SerializedName("response")
    var response: Response<T>
)

data class Response<T>(
    @SerializedName("header")
    var header: Header,

    @SerializedName("body")
    var body: Body<T>
)

data class Header(
    @SerializedName("resultCode")
    var resultCode: String,

    @SerializedName("resultMsg")
    var resultMsg: String
)

data class Body<T>(
    @SerializedName("items")
    var items: T? = null,

    @SerializedName("numOfRows")
    var numOfRows: Int,

    @SerializedName("pageNo")
    var pageNo: Int,

    @SerializedName("totalCount")
    var totalCount: Int
)

data class TourItems(
    @SerializedName("item")
    var item: List<TourItem>? = null
)

data class LocationBasedTourItems(
    @SerializedName("item")
    var item: List<LocationBasedTourItem>? = null
)

data class TourDetailItems(
    @SerializedName("item")
    var item: List<TourDetailItem>? = null
)

data class TourImageItems(
    @SerializedName("item")
    var item: List<TourImageItem>? = null
)
