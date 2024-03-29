package com.sessac.myaitrip.common

const val DEFAULT_TOTAL_COUNT = -1
const val DEFAULT_PAGE_NUMBER = 1
const val DEFAULT_NUM_OF_ROWS = 1000
const val DEFAULT_PAGING_SIZE  = 10
const val DEFAULT_PREFETCH_DISTANCE = 3
const val TOUR_CONTENT_ID = "tourContentId"
const val TOUR_IMAGE_LIST = "tourImageList"
const val TOUR_ITEM = "tourItem"
const val CONTENT_ID_LIST = "contentIdList"
const val LIST_TYPE = "listType"

const val API_ERROR_MASSAGE = "네트워크 연결에 실패하였습니다. 오프라인 모드로 동작합니다."
val CONTENT_TYPE_ID_CATEGORY  = mapOf(
    "관광지" to "12",
    "문화시설" to "14",
    "축제공연행사" to "15",
    "여행코스" to "25",
    "레포츠" to "28",
    "숙박" to "32",
    "쇼핑" to "38",
    "음식점" to "39"
)

val CONTENT_TYPE_ID_AREA  = mapOf(
    "서울" to "1",
    "인천" to "2",
    "대전" to "3",
    "대구" to "4",
    "광주" to "5",
    "부산" to "6",
    "울산" to "7",
    "세종" to "8",
    "경기도" to "31",
    "강원도" to "32",
    "충청북도" to "33",
    "충청남도" to "34",
    "경상북도" to "35",
    "경상남도" to "36",
    "전라북도" to "37",
    "전라남도" to "38",
    "제주도" to "39"
)
