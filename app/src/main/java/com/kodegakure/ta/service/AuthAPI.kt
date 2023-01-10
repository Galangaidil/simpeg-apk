package com.kodegakure.ta.service

import com.kodegakure.ta.auth.AuthRequest
import com.kodegakure.ta.auth.AuthResponse
import com.kodegakure.ta.auth.LogoutResponse
import com.kodegakure.ta.auth.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthAPI {
    @Headers("Accept: application/json")
    @POST("login")
    fun login(@Body req: AuthRequest) : Call<AuthResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String) : Call<LogoutResponse>

    @GET("user")
    fun user(@Header("Authorization") token: String) : Call<UserResponse>
} 