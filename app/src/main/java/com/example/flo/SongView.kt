package com.example.flo

interface SongView {
    fun onGetTrackLoading()
    fun onGetTrackSuccess(code : Int, result : TrackResult)
    fun onGetTrackFailure(code : Int, message : String)
}