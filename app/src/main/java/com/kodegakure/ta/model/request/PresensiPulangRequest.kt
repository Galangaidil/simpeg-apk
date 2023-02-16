package com.kodegakure.ta.model.request

data class PresensiPulangRequest(
    val latitude_pulang: Double,
    val longitude_pulang: Double,
    val waktu: String
)
