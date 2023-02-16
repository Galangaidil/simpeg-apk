package com.kodegakure.ta.model.request

data class LoginRequest(
    val email: String,
    val password: String,
    val device_name: String
)
