package com.kodegakure.ta

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodegakure.ta.api.NetworkConfigurations
import com.kodegakure.ta.attendance.read.AttendancesAdapter
import com.kodegakure.ta.model.response.RiwayatPresensiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.RecyclerViewAttendances)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = AttendancesAdapter(arrayListOf())
        recyclerView.adapter = adapter
//        read()
        getRiwayatPresensi()
    }

//    private fun read() {
//        val sp = this.requireActivity().getSharedPreferences("auth", 0)
//        val token = sp.getString("token", "")
//
//        val retro = APIClient().getClient().create(AttendanceAPI::class.java)
//        retro.read("Bearer $token").enqueue(object : Callback<List<AttendancesResponse>> {
//            override fun onResponse(
//                call: Call<List<AttendancesResponse>>, response: Response<List<AttendancesResponse>>
//            ) {
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    if (data != null) {
//                        adapter.setData(data as ArrayList<AttendancesResponse>)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<AttendancesResponse>>, t: Throwable) {
//                Log.e("E", "onFailure: ${t.message}")
//            }
//
//        })
//    }

    private fun getRiwayatPresensi() {
        val sp = this.requireActivity().getSharedPreferences("auth", 0)
        val token = sp.getString("token", "")
        NetworkConfigurations().getService().riwayatPresensi(token = "Bearer $token")
            .enqueue(object : Callback<List<RiwayatPresensiResponse>> {
                override fun onResponse(
                    call: Call<List<RiwayatPresensiResponse>>,
                    response: Response<List<RiwayatPresensiResponse>>
                ) {
                    if (response.isSuccessful){
                        val data = response.body()
                        if (data != null){
                            adapter.setData(data as ArrayList<RiwayatPresensiResponse>)
                        }
                    }
                }

                override fun onFailure(call: Call<List<RiwayatPresensiResponse>>, t: Throwable) {
                    Log.e("Error", "onFailure: ${t.message}")
                }

            })
    }
}
