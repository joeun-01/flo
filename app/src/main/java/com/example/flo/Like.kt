package com.example.flo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LikeTable")
data class Like(  // userId와 album Id를 이용하여 유저가 좋아요한 앨범을 구분
    var userId : Int,
    var albumId : Int
){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}

