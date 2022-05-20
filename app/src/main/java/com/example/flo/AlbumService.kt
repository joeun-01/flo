package com.example.flo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumService {
    private lateinit var homeView: HomeView
    private lateinit var songView : SongView

    fun setHomeView(homeView : HomeView) {
        this.homeView = homeView
    }

    fun setSongView(songView: SongView) {
        this.songView = songView
    }

    fun getAlbums() {
        val albumService = getRetrofit().create(AlbumRetrofitInterface::class.java)

        homeView.onGetAlbumLoading()

        albumService.getAlbums().enqueue(object : Callback<AlbumResponse> {
            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                if(response.isSuccessful && response.code() == 200) {
                    val albumResponse : AlbumResponse = response.body()!!

                    Log.d("ALBUM-RESPONSE", albumResponse.toString())

                    when(val code = albumResponse.code) {
                        1000 -> homeView.onGetAlbumSuccess(code, albumResponse.result)
                        else -> homeView.onGetAlbumFailure(code, albumResponse.message)
                    }
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                homeView.onGetAlbumFailure(400, "네트워크 오류가 발생했습니다.")
            }

        })
    }

    fun getAlbumSongs(albumIdx : Int) {
        val trackService = getRetrofit().create(AlbumRetrofitInterface::class.java)

        songView.onGetTrackLoading()

        trackService.getAlbumSongs(albumIdx).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if(response.isSuccessful && response.code() == 200) {
                    val trackResponse : TrackResponse = response.body()!!

                    Log.d("Track-RESPONSE", trackResponse.toString())

                    when(val code = trackResponse.code) {
                        1000 -> songView.onGetTrackSuccess(code, trackResponse.result)
                        else -> songView.onGetTrackFailure(code, trackResponse.message)
                    }
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                songView.onGetTrackFailure(400, "네트워크 오류가 발생했습니다.")
            }

        })
    }
}