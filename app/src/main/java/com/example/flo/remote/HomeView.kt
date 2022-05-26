package com.example.flo.remote

import com.example.flo.remote.AlbumResult

interface HomeView {
    fun onGetAlbumLoading()
    fun onGetAlbumSuccess(code : Int, result : AlbumResult)
    fun onGetAlbumFailure(code : Int, message : String)
}