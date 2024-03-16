package com.sessac.myaitrip.data.entities.remote

import com.google.gson.annotations.SerializedName

data class WeatherApiResponseBody(
    @SerializedName("dataType") val dataType: String,
    @SerializedName("items") val items: WeatherItems

)

data class WeatherItems(
    @SerializedName("item") val weatherItems: List<Weather>
)

data class Weather(
    @SerializedName("baseDate") val baseDate: String,
    @SerializedName("baseTime") val baseTime: String,
    @SerializedName("category") val dataCategory: String,
    @SerializedName("fcstDate") val forecastDate: String,
    @SerializedName("fcstTime") val forecastTime: String,
    @SerializedName("fcstValue") val forecastValue: String,
    @SerializedName("nx") val nx: Int,
    @SerializedName("ny") val ny: Int
)


