package com.kodegakure.ta.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.kodegakure.ta.DashboardActivity
import com.kodegakure.ta.R
import com.kodegakure.ta.api.NetworkConfigurations
import com.kodegakure.ta.model.request.LoginRequest
import com.kodegakure.ta.model.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var errMessage: TextView
    private lateinit var buttonLogin: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var deviceName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        errMessage = findViewById(R.id.textViewErrorMessage)
        buttonLogin = findViewById(R.id.buttonSubmitLogin)
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.PRODUCT}"

        buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        NetworkConfigurations().getService().login(getLoginRequest())
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        // save token and username to sharedPreferences
                        val sharedPreferences: SharedPreferences =
                            getSharedPreferences("auth", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", response.body()!!.data!!.token)
                        editor.putString("userName", response.body()!!.data!!.user_name)
                        editor.apply()

                        Toast.makeText(
                            this@LoginActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val gson = Gson()
                        val message: LoginResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            LoginResponse::class.java
                        )
                        errMessage.text = message.message.toString()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    errMessage.text = t.message.toString()
                }

            })
    }

    private fun getLoginRequest(): LoginRequest {
        return LoginRequest(
            email = email.text.toString(),
            password = password.text.toString(),
            device_name = deviceName
        )
    }
}