package com.sessac.myaitrip.data.entities.remote.firebase

data class UserData(
    val id: String,
    val email: String,
    val nickname: String,
    var profileImgUrl: String
)
