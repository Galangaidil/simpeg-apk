package com.kodegakure.ta

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
import com.kodegakure.ta.auth.AuthRequest
import com.kodegakure.ta.auth.AuthResponse
import com.kodegakure.ta.service.AuthAPI
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
        val authReq = AuthRequest()

        authReq.email = email.text.toString()
        authReq.password = password.text.toString()
        authReq.deviceName = deviceName

        val retro = APIClient().getClient().create(AuthAPI::class.java)
        retro.login(authReq).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                if (response.isSuccessful) {
                    /**
                     * Insert auth to sharedPreferences
                     * auth file contains "token" and "userName"
                     */
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token", response.body()!!.data!!.token)
                    editor.putString("userName", response.body()!!.data!!.userName)
                    editor.apply()

                    Toast.makeText(
                        this@LoginActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    /**
                     * Start DashboardActivity
                     */
                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val gson = Gson()
                    val message: AuthResponse = gson.fromJson(
                        response.errorBody()!!.charStream(),
                        AuthResponse::class.java
                    )
                    errMessage.text = message.message.toString()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                errMessage.text = t.message.toString()
            }

        })
    }
}