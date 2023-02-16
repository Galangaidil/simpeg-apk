package com.kodegakure.ta.api

import com.kodegakure.ta.model.request.CekWaktuPresensiRequest
import com.kodegakure.ta.model.request.LoginRequest
import com.kodegakure.ta.model.request.PresensiMasukRequest
import com.kodegakure.ta.model.request.PresensiPulangRequest
import com.kodegakure.ta.model.response.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class NetworkConfigurations {
    private fun getClient(): Retrofit {
//        val localUrl = "http://10.0.2.2:8000/api/v1/"
        val forRealDeviceUrl = "http://192.168.11.64:8000/api/v1/"
//        val productionUrl: String = "https://simpeg.kodegakure.com/api/v1/"

        return Retrofit.Builder()
            .baseUrl(forRealDeviceUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getService(): Endpoint = getClient().create(Endpoint::class.java)
}

interface Endpoint {
    // Login
    @Headers("Accept: application/json")
    @POST("login")
    fun login(@Body LoginRequest: LoginRequest): Call<LoginResponse>

    // Get user profile
    @Headers("Accept: application/json")
    @GET("user")
    fun userProfile(@Header("Authorization") token: String): Call<UserProfileResponse>

    // Cek waktu presensi
    @Headers("Accept: application/json")
    @POST("checkwaktupresensi")
    fun cekwaktupresensi(
        @Body waktu: CekWaktuPresensiRequest,
        @Header("Authorization") token: String
    ): Call<CekWaktuPresensiResponse>

    // Presensi masuk
    @Headers("Accept: application/json")
    @POST("presensi/masuk")
    fun presensiMasuk(
        @Body PresensiMasukRequest: PresensiMasukRequest,
        @Header("Authorization") token: String
    ): Call<PresensiResponse>

    // Presensi terlambat
    @Headers("Accept: application/json")
    @POST("presensi/terlambat")
    fun presensiTerlambat(
        @Body PresensiMasukRequest: PresensiMasukRequest,
        @Header("Authorization") token: String
    ): Call<PresensiResponse>

    // Presensi pulang
    @Headers("Accept: application/json")
    @POST("presensi/pulang")
    fun presensiPulang(
        @Body PresensiPulangRequest: PresensiPulangRequest,
        @Header("Authorization") token: String
    ): Call<PresensiResponse>

    // Presensi lembur
    @Headers("Accept: application/json")
    @POST("presensi/lembur")
    fun presensiLembur(
        @Body PresensiPulangRequest: PresensiPulangRequest,
        @Header("Authorization") token: String
    ): Call<PresensiResponse>

    // Riwayat presensi
    @Headers("Accept: application/json")
    @GET("presensi/riwayat")
    fun riwayatPresensi(@Header("Authorization") token: String) : Call<List<RiwayatPresensiResponse>>

    // Logout
    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>
}