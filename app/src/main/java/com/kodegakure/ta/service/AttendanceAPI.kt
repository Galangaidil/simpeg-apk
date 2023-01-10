package com.kodegakure.ta.service

import com.kodegakure.ta.attendance.create.AttendanceRequest
import com.kodegakure.ta.attendance.create.AttendanceResponse
import com.kodegakure.ta.attendance.read.AttendancesResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AttendanceAPI {
    @Headers("Accept: application/json")
    @POST("attendances")
    fun store(
        @Body req: AttendanceRequest,
        @Header("Authorization") token:String
    ) : Call<AttendanceResponse>

    @Headers("Accept: application/json")
    @GET("attendances")
    fun read(@Header("Authorization") token: String) : Call<List<AttendancesResponse>>
}