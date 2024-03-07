package com.sessac.myaitrip.data.entities

data class User(
    val id: String,
    val email: String,
    val nickname: String,
    var profileImgUrl: String
)
