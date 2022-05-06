package com.example.flo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    var title : String? = "",  // 노래 제목
    var singer : String? = "",  // 가수
    var coverImg : Int? = null,  // 커버 이미지
    @Ignore var songs : ArrayList<Song>? = null  // 수록곡
){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}
