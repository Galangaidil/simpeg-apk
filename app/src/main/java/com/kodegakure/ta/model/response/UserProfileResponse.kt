package com.kodegakure.ta.model.response

data class UserProfileResponse(
    val id: Double?,
    val role_id: Int?,
    val name: String?,
    val email: String?,
    val nip: String?,
    val phone_number: String?,
    val address: String?,
    val birthdate: String?,
    val birthplace: String?
)