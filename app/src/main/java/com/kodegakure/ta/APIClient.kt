package com.kodegakure.ta

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {
    fun getClient(): Retrofit {
        val localEndpoint: String = "http://10.0.2.2:8000/api/v1/"
        val prodEndpoint: String = "https://simpeg.kodegakure.com/api/v1/"

        return Retrofit.Builder()
            .baseUrl(localEndpoint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}