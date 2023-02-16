package com.kodegakure.ta

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.kodegakure.ta.api.NetworkConfigurations
import com.kodegakure.ta.model.response.UserProfileResponse
import com.kodegakure.ta.view.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted: Boolean = false
    private var isReadPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingPanel = findViewById<RelativeLayout>(R.id.loadingPanel)
        loadingPanel.isVisible = false

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                isReadPermissionGranted =
                    permission[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
                isLocationPermissionGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: isLocationPermissionGranted
            }

        requestPermission()

        val sp = getSharedPreferences("auth", Context.MODE_PRIVATE)

        if (sp.contains("token")) {
            Log.i("token", "Token exists, trying to authenticate")
            loadingPanel.isVisible = true

            val token = sp.getString("token", "")

            NetworkConfigurations().getService()
                .userProfile("Bearer $token").enqueue(object : Callback<UserProfileResponse> {
                    override fun onResponse(
                        call: Call<UserProfileResponse>,
                        response: Response<UserProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.i("Auth success", "onResponse: Authenticated success")
                            val intent = Intent(applicationContext, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                        loadingPanel.isVisible = false
                        Log.i("token not valid", "onResponse: Token not valid anymore")
                        Log.e("E", "onFailure: ${t.message}")
                        val editor = sp.edit()
                        editor.remove("token")
                        editor.remove("userName")
                        editor.apply()
                    }

                })

        }

        val buttonLogin: Button = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isReadPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!isLocationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}