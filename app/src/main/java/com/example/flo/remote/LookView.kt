package com.example.flo.remote

import com.example.flo.remote.FloChartResult

interface LookView {
    fun onGetSongLoading()
    fun onGetSongSuccess(code : Int, result : FloChartResult)
    fun onGetSongFailure(code : Int, message : String)
}