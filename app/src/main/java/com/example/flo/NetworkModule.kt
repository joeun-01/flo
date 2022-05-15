package com.example.flo

import androidx.annotation.RestrictTo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://edu-api-test.softsquared.com"
// 여기에서 BASE_URL에 /users까지 적어줄거면 interface에서 그 부분을 빼주면 됨

fun getRetrofit() : Retrofit {
    val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit
}