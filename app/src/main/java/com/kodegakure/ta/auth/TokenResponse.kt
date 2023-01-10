package com.kodegakure.ta.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TokenResponse {
    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("user_name")
    @Expose
    var userName: String? = null
}