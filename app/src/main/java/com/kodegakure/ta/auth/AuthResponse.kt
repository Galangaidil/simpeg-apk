package com.kodegakure.ta.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthResponse {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: TokenResponse? = null
}