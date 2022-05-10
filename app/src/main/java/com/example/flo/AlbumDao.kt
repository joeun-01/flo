package com.example.flo

import androidx.room.*

@Dao
interface AlbumDao {

    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable")
    fun getAlbums() : List<Album>

    // LikeTable에 사용할 쿼리
    @Insert
    fun likeAlbum(like: Like)

    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")  // LikeTable에 이 앨범이 있는 지 확인
    fun isLikedAlbum(userId: Int, albumId: Int) : Int?  // 그리고 albumId를 반환

    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId")  // LikeTable에서 앨범을 삭제
    fun dislikedAlbum(userId: Int, albumId: Int) : Int?

    // LikeTable에 있는 albumId를 기준으로 좋아요한 Album들을 가지고 옴
    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT on LT.albumId = AT.id WHERE LT.userId = :userId")
    fun getLikedAlbums(userId: Int) : List<Album>
}