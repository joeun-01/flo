package com.example.flo.remote

interface AutoLoginView {  // activity와 AuthService를 연결
    fun onAutoLoginSuccess(code : Int)
    fun onAutoLoginFailure()
}