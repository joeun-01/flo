package com.example.flo

interface SignUpView {  // activity와 AuthService를 연결
    fun onSignUpSuccess()
    fun onSignUpFailure(message: String)
}