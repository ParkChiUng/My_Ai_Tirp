package com.sessac.myaitrip.data.entities

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

class TourClusterItemKey(val contentId: String, private val latLng: LatLng): ClusteringKey {
    override fun getPosition() = latLng

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if( other == null || javaClass != other.javaClass) return false
        val itemKey = other as TourClusterItemKey
        return contentId == other.contentId
    }

    override fun hashCode(): Int {
        return contentId.toInt()
    }
}