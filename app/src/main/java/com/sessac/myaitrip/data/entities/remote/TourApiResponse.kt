package com.sessac.myaitrip.data.entities.remote

import com.google.gson.annotations.SerializedName
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
