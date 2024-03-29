package com.sessac.myaitrip.data.entities

import android.net.Uri

data class DiaryItem(
    val contentId: String,
    val diaryTitle: String,
    val diaryReview: String,
    val diaryImage: MutableList<Uri>
)