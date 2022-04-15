package com.example.flo

data class Album(
    var title : String? = "",  // 노래 제목
    var singer : String? = "",  // 가수
    var coverImg : Int? = null,  // 커버 이미지
    var songs : ArrayList<Song>? = null  // 수록곡
)
