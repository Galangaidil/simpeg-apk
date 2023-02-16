package com.kodegakure.ta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.gson.Gson
import com.kodegakure.ta.api.NetworkConfigurations
import com.kodegakure.ta.model.request.CekWaktuPresensiRequest
import com.kodegakure.ta.model.request.PresensiMasukRequest
import com.kodegakure.ta.model.request.PresensiPulangRequest
import com.kodegakure.ta.model.response.CekWaktuPresensiResponse
import com.kodegakure.ta.model.response.PresensiResponse
import com.kodegakure.ta.view.HasilPresensiActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    lateinit var jenisPresensi: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lat by Delegates.notNull<Double>()
    private var lon by Delegates.notNull<Double>()
    private lateinit var waktuSaatIni: TextView
    private lateinit var tanggalSaatIni: TextView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)
        val welcomeMessage = homeView.findViewById<TextView>(R.id.welcomeMessage)
        val tombolPresensi = homeView.findViewById<Button>(R.id.tombolPresensi)
        waktuSaatIni = homeView.findViewById(R.id.waktu_saat_ini)
        tanggalSaatIni = homeView.findViewById(R.id.tanggal_saat_ini)
        setWaktuSaatIni()
        setTanggalSaatIni()

        // mematikan tombol presensi
        tombolPresensi.isVisible = false

        // tombol untuk melakukan presensi
        tombolPresensi.setOnClickListener {
            presensi(jenisPresensi)
        }

        // inisiasi untuk location services
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        }
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(requireActivity(), "Cannot get location.", Toast.LENGTH_SHORT)
                        .show()
                else {
                    this@HomeFragment.lat = location.latitude
                    this@HomeFragment.lon = location.longitude

                    Log.i("Coodinates", "onCreateView: $lat, $lon")

                    // Pengecekan waktu presensi
                    getWaktuPresensi(welcomeMessage, tombolPresensi)
                }

            }

        return homeView
    }

    // cek waktu presensi
    private fun getWaktuPresensi(target: TextView, tombol: Button) {
        NetworkConfigurations().getService()
            .cekwaktupresensi(CekWaktuPresensiRequest(getWaktu()), token = "Bearer ${getToken()}")
            .enqueue(object : Callback<CekWaktuPresensiResponse> {
                override fun onResponse(
                    call: Call<CekWaktuPresensiResponse>,
                    response: Response<CekWaktuPresensiResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()

                        if (res != null) {
                            target.text = res.keterangan
                            tombol.text = res.button_text
                            jenisPresensi = res.endpoint
                            tombol.isVisible = true
                        } else {
                            tombol.isVisible = false
                        }
                    } else {
                        val res = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            CekWaktuPresensiResponse::class.java
                        )

                        target.text = res.keterangan
                        tombol.isVisible = false
                    }
                }

                override fun onFailure(call: Call<CekWaktuPresensiResponse>, t: Throwable) {
                    Log.e("Server Error", "onFailure: ${t.message}")
                    tombol.isVisible = false
                }

            })
    }

    // menentukan jenis presensi yang akan dilakukan.
    private fun presensi(endpoint: String) {
        when (endpoint) {
            "presensi/masuk" -> presensiMasuk()
            "presensi/terlambat" -> presensiTerlambat()
            "presensi/pulang" -> presensiPulang()
            "presensi/lembur" -> presensiLembur()
            else -> Toast.makeText(
                activity,
                "Bukan waktu presensi.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // melakukan presensi masuk
    private fun presensiMasuk() {
        NetworkConfigurations().getService()
            .presensiMasuk(getPresensiMasukRequest(), token = "Bearer ${getToken()}")
            .enqueue(object : Callback<PresensiResponse> {
                override fun onResponse(
                    call: Call<PresensiResponse>,
                    response: Response<PresensiResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()

                        if (res != null) {
                            intentToHasilPresensiActivity(res.message)
                        }

                    } else {
                        val res = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            PresensiResponse::class.java
                        )
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PresensiResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    // melakukan presensi masuk
    private fun presensiTerlambat() {
        NetworkConfigurations().getService()
            .presensiTerlambat(getPresensiMasukRequest(), token = "Bearer ${getToken()}")
            .enqueue(object : Callback<PresensiResponse> {
                override fun onResponse(
                    call: Call<PresensiResponse>,
                    response: Response<PresensiResponse>
                ) {
                    if (response.isSuccessful) {
                        intentToHasilPresensiActivity(response.body()!!.message)
                    } else {
                        val res = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            PresensiResponse::class.java
                        )
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PresensiResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    // melakukan presensi pulang
    private fun presensiPulang() {
        NetworkConfigurations().getService()
            .presensiPulang(getPresensiPulangRequest(), token = "Bearer ${getToken()}")
            .enqueue(object : Callback<PresensiResponse> {
                override fun onResponse(
                    call: Call<PresensiResponse>,
                    response: Response<PresensiResponse>
                ) {
                    if (response.isSuccessful) {
                        intentToHasilPresensiActivity(response.body()!!.message)
                    } else {
                        val res = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            PresensiResponse::class.java
                        )
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PresensiResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    // melakukan presensi lembur
    private fun presensiLembur() {
        NetworkConfigurations().getService()
            .presensiLembur(getPresensiPulangRequest(), token = "Bearer ${getToken()}")
            .enqueue(object : Callback<PresensiResponse> {
                override fun onResponse(
                    call: Call<PresensiResponse>,
                    response: Response<PresensiResponse>
                ) {
                    if (response.isSuccessful) {
                        intentToHasilPresensiActivity(response.body()!!.message)
                    } else {
                        val res = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            PresensiResponse::class.java
                        )
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PresensiResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    // mendapatkan waktu dalam format HH:mm:ss
    private fun getWaktu(): String {
        val f: Format = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        return f.format(Date())
    }

    // mendapatkan token
    private fun getToken(): String? {
        val sp = this.requireActivity().getSharedPreferences("auth", 0)
        return sp.getString("token", "")
    }

    // pergi ke ativity hasil presensi
    private fun intentToHasilPresensiActivity(message: String) {
        val intent = Intent(requireActivity(), HasilPresensiActivity::class.java)
        intent.putExtra("message", message)
        startActivity(intent)
    }

    // membuat object presensi masuk request
    private fun getPresensiMasukRequest(): PresensiMasukRequest {
        return PresensiMasukRequest(
            latitude_masuk = lat,
            longitude_masuk = lon,
            waktu = getWaktu()
        )
    }

    // membuat object presensi pulang request
    private fun getPresensiPulangRequest(): PresensiPulangRequest {
        return PresensiPulangRequest(
            latitude_pulang = lat,
            longitude_pulang = lon,
            waktu = getWaktu()
        )
    }

    // menampilkan waktu saat ini
    private fun setWaktuSaatIni() {
        waktuSaatIni.text = getString(R.string.waktu_sekarang, getFormattedTime())
    }

    private fun setTanggalSaatIni(){
        tanggalSaatIni.text = getString(R.string.tanggal_sekarang, getFormattedDate())
    }

    private fun getFormattedTime(): String{
        val f: Format = SimpleDateFormat("HH.mm", Locale.ROOT)
        return f.format(Date())
    }

    private fun getFormattedDate(): String{
        val f: Format = SimpleDateFormat("EEE, dd/MM/yyyy", Locale.ROOT)
        return f.format(Date())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}