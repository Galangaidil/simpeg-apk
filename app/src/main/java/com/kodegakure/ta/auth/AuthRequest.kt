package com.kodegakure.ta.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthRequest {
    var email: String? = null
    var password: String? = null

    @SerializedName("device_name")
    @Expose
    var deviceName: String? = null
}