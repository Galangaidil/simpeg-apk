package com.kodegakure.ta.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogoutResponse {
    @SerializedName("message")
    @Expose
    var message: String? = null
}