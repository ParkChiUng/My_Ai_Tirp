package com.sessac.myaitrip.data.entities.remote

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse<T>(
    @SerializedName("response")
    var response: WeatherResponse<T>? = null
)

data class WeatherResponse<T>(
    @SerializedName("header")
    var header: Header? = null,

    @SerializedName("body")
    var body: WeatherResponseBody<T>? = null
)

data class WeatherResponseBody<T>(
    @SerializedName("dataType") val dataType: String,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("numOfRows") val numOfRows: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("items") val item: T
)

data class WeatherItems(
    @SerializedName("item") val weatherInfoItems: List<WeatherInfo>
)

data class WeatherInfo(
    @SerializedName("baseDate") val baseDate: String,
    @SerializedName("baseTime") val baseTime: String,
    @SerializedName("category") val dataCategory: String,
    @SerializedName("fcstDate") val forecastDate: String,
    @SerializedName("fcstTime") val forecastTime: String,
    @SerializedName("fcstValue") val forecastValue: String,
    @SerializedName("nx") val nx: Int,
    @SerializedName("ny") val ny: Int
)


