package com.kodegakure.ta

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.kodegakure.ta.attendance.create.AttendanceRequest
import com.kodegakure.ta.attendance.create.AttendanceResponse
import com.kodegakure.ta.attendance.read.AttendancesAdapter
import com.kodegakure.ta.attendance.read.AttendancesResponse
import com.kodegakure.ta.service.AttendanceAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AttendanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendanceFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: AttendancesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AttendanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AttendanceFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())


        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.RecyclerViewAttendances)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = AttendancesAdapter(arrayListOf())
        recyclerView.adapter = adapter
        read()

        val fab: View = view.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            getLocation(view)
        }
    }

    private fun read() {
        val sp = this.requireActivity().getSharedPreferences("auth", 0)
        val token = sp.getString("token", "")

        val retro = APIClient().getClient().create(AttendanceAPI::class.java)
        retro.read("Bearer $token").enqueue(object : Callback<List<AttendancesResponse>> {
            override fun onResponse(
                call: Call<List<AttendancesResponse>>, response: Response<List<AttendancesResponse>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        adapter.setData(data as ArrayList<AttendancesResponse>)
                    }
                }
            }

            override fun onFailure(call: Call<List<AttendancesResponse>>, t: Throwable) {
                Log.e("E", "onFailure: ${t.message}")
            }

        })
    }

    private fun getLocation(view: View) {
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                this.requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                create(it.latitude, it.longitude)
            }
        }
    }

    private fun create(latitude: Double, longitude: Double) {
        val sp = this.requireActivity().getSharedPreferences("auth", 0)
        val token = sp.getString("token", "")

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val attendanceReq = AttendanceRequest()
        attendanceReq.time = "$hour:$minute"
        attendanceReq.latitude = latitude
        attendanceReq.longitude = longitude

        val retro = APIClient().getClient().create(AttendanceAPI::class.java)
        retro.store(attendanceReq, "Bearer $token").enqueue(object : Callback<AttendanceResponse> {
            override fun onResponse(
                call: Call<AttendanceResponse>, response: Response<AttendanceResponse>
            ) {
                if (response.isSuccessful) {
                    read()
                    Snackbar.make(
                        activity!!.findViewById(R.id.attendances),
                        response.body()?.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val gson = Gson()
                    val attendanceResponse: AttendanceResponse = gson.fromJson(
                        response.errorBody()!!.charStream(), AttendanceResponse::class.java
                    )
                    Snackbar.make(
                        activity!!.findViewById(R.id.attendances),
                        attendanceResponse.data?.hint.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<AttendanceResponse>, t: Throwable) {
                Log.e("Presensi gagal:", "onFailure: ${t.message}")
            }

        })


    }
}
