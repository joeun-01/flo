package com.example.flo

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName(value = "isSuccess") val isSuccess : Boolean,
    @SerializedName(value = "code") val code : Int,
    @SerializedName(value = "message") val message : String,
    @SerializedName(value = "result") val result : Result?  // 회원가입에서는 result를 사용하지 않기 때문에 null 처리
)

data class Result(
    @SerializedName(value = "userIdx") var userIdx : Int,
    @SerializedName(value = "jwt") var jwt : String
)
