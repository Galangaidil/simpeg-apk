package com.kodegakure.ta.model.response

data class RiwayatPresensiResponse(
    val id: Long?,
    val user_id: Long?,
    val latitude_masuk: String?,
    val latitude_pulang: String?,
    val longitude_masuk: String?,
    val longitude_pulang: String?,
    val distance_masuk: Double?,
    val distance_pulang: Double?,
    val jam_masuk: String?,
    val jam_pulang: String?,
    val lembur: Int?,
    val status: String?,
    val created_at: String?
)