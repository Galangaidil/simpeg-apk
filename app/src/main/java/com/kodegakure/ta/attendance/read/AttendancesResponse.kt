package com.kodegakure.ta.attendance.read

data class AttendancesResponse(
    var id: Int,
    var user_id: Int,
    var latitude: String,
    var longitude: String,
    var distance: Double,
    var status: String,
    var created_at: String,
    var updated_at: String,
    var diffForHuman: String
)
