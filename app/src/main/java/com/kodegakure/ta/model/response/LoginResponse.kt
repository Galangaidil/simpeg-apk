package com.kodegakure.ta.model.response

data class LoginData(
    val token: String?,
    val user_name: String?
)

data class LoginResponse(
    val message: String?,
    val data: LoginData?
)
