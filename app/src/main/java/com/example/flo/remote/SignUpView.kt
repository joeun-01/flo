package com.example.flo.remote

interface SignUpView {  // activity와 AuthService를 연결
    fun onSignUpSuccess()
    fun onSignUpFailure(message: String)
}