package com.example.flo.remote

import com.example.flo.remote.Result

interface LoginView {
    fun onLoginSuccess(code : Int, result : Result)
    fun onLoginFailure(message: String)
}