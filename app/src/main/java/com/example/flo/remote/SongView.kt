package com.example.flo.remote

import com.example.flo.remote.TrackResult

interface SongView {
    fun onGetTrackLoading()
    fun onGetTrackSuccess(code : Int, result : ArrayList<TrackResult>)
    fun onGetTrackFailure(code : Int, message : String)
}