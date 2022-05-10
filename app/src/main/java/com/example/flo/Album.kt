package com.example.flo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = false) var id: Int = 0,  // album ID
    var title : String? = "",  // 노래 제목
    var singer : String? = "",  // 가수
    var coverImg : Int? = null,  // 커버 이미지
)
