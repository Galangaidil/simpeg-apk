package com.kodegakure.ta.model.request

data class PresensiMasukRequest(
    val latitude_masuk: Double,
    val longitude_masuk: Double,
    val waktu: String
)
