package com.example.flo

interface HomeView {
    fun onGetAlbumLoading()
    fun onGetAlbumSuccess(code : Int, result : AlbumResult)
    fun onGetAlbumFailure(code : Int, message : String)
}