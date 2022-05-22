package com.example.flo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumRetrofitInterface {
    @GET("/albums")
    fun getAlbums() : Call<AlbumResponse>

    @GET("/albums/{albumIdx}")
    fun getAlbumSongs(@Path("albumIdx") albumIdx : Int) : Call<TrackResponse>

}