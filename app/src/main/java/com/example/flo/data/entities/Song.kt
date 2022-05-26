package com.example.flo.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    val number : String = "", // 수록곡 넘버
    val title : String = "",  // 제목
    val singer : String = "",  // 가수
    var albumImg : Int? = null,  // 커버 이미지
    var second : Int = 0,  // 재생된 시간
    var playTime : Int = 0,  // 총 재생 시간
    var isPlaying : Boolean = false,  // 현재 재생 여부
    var music : String = "",  // mp3 파일명
    var current : Int = 0,  // MediaPlayer로 재생할 지점
    var isLike : Boolean = false,
    var albumIdx : Int = 0,
    var isInPlaylist : Boolean
){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}
