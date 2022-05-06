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

    @Query("SELECT * FROM SongTable WHERE isLike = :isLike")
    fun getLikeSongs(isLike : Boolean) : List<Song>

    @Query("UPDATE SongTable SET second = :second WHERE id = :id")
    fun updateSecondById(second : Int, id : Int)

    @Query("UPDATE SongTable SET current = :current WHERE id = :id")
    fun updateCurrentById(current : Int, id : Int)

    @Query("UPDATE SongTable SET isPlaying = :isPlaying WHERE id = :id")
    fun updateIsPlayingById(isPlaying : Boolean, id : Int)

}