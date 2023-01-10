package com.kodegakure.ta.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    var id: Int? = null

    @SerializedName("role_id")
    @Expose
    var roleId: Int? = null

    var name: String? = null

    var email: String? = null

    var nip: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    var address: String? = null

    @SerializedName("birthdate")
    @Expose
    var birthDate: String? = null

    @SerializedName("birthplace")
    @Expose
    var birthPlace: String? = null
}