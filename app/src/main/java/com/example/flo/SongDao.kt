package com.example.flo

import androidx.room.*

@Dao
interface SongDao {
    @Insert
    fun insert(song : Song)

    @Update
    fun update(song : Song)

    @Delete
    fun delete(song : Song)

    @Query("SELECT * FROM SongTable")
        fun getSongs() : List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id")  // id를 통해서 song을 받아옴
        fun getSong(id : Int) : Song

    @Query("UPDATE SongTable SET isLike = :isLike WHERE id = :id")  // isLike 값 업데이트
        fun updateIsLikeById(isLike : Boolean, id : Int)
}