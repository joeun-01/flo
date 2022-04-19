package com.example.flo

data class Song(
    val title : String = "",  // 제목
    val singer : String = "",  // 가수
    var albumImg : Int? = null,
    var second : Int = 0,  // 재생된 시간
    var playTime : Int = 0,  // 총 재생 시간
    var isPlaying : Boolean = false,  // 현재 재생 여부
    var music : String = "",  // mp3 파일명
    var current : Int = 0  // MediaPlayer로 재생할 지점
)
