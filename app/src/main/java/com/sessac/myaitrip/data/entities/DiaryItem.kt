package com.sessac.myaitrip.data.entities

import android.net.Uri

data class DiaryItem(
    val diaryId: String? = null,
    val contentId: String,
    val tourTitle: String,
    val tourAddress: String,
    val diaryTitle: String,
    val diaryReview: String,
    val diaryImage: MutableList<Uri>,
    val createDateTime: String
)