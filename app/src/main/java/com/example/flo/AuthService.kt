package com.example.flo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView
    private lateinit var autoLoginView: AutoLoginView

    fun setSignUpView(signUpView : SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView: LoginView) {
        this.loginView = loginView
    }

    fun setAutoLoginView(autoLoginView: AutoLoginView) {
        this.autoLoginView = autoLoginView
    }

    fun signUp(user: User) {
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        authService.signUp(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                // 응답이 왔을 때 처리
                Log.d("SIGNUP / SUCCESS", response.toString())

                val resp : AuthResponse = response.body()!!

                when(resp.code) {
                    1000 -> signUpView.onSignUpSuccess()  // 회원가입 성공
                    else -> signUpView.onSignUpFailure(resp.message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                // 네트워크 연결이 실패했을 때 실행
                Log.d("SIGNUP / FAILURE", t.message.toString())
            }

        })  // 유저 정보를 넣어주면서 api 호출, eunqueue에서 응답 처리

        Log.d("SIGNUP", "HELLO")
    }

    fun login(user: User) {
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        authService.login(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                // 응답이 왔을 때 처리
                Log.d("LOGIN / SUCCESS", response.toString())

                val resp : AuthResponse = response.body()!!

                when(val code = resp.code) {
                    1000 -> loginView.onLoginSuccess(code, resp.result!!)  // 성공
                    else -> loginView.onLoginFailure(resp.message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                // 네트워크 연결이 실패했을 때 실행
                Log.d("LOGIN / FAILURE", t.message.toString())
            }

        })  // 유저 정보를 넣어주면서 api 호출, eunqueue에서 응답 처리

        Log.d("LOGIN", "HELLO")
    }

    fun autoLogin(jwt: String?) {
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        authService.autoLogin(jwt).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                // 응답이 왔을 때 처리
                Log.d("AUTOLOGIN / SUCCESS", response.toString())

                val resp : AuthResponse = response.body()!!

                when(val code = resp.code) {
                    1000 -> autoLoginView.onAutoLoginSuccess(code)  // 성공
                    else -> autoLoginView.onAutoLoginFailure()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                // 네트워크 연결이 실패했을 때 실행
                Log.d("AUTOLOGIN / FAILURE", t.message.toString())
            }

        })  // 유저 정보를 넣어주면서 api 호출, eunqueue에서 응답 처리

        Log.d("LOGIN", "HELLO")
    }
}