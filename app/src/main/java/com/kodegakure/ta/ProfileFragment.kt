package com.kodegakure.ta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kodegakure.ta.api.NetworkConfigurations
import com.kodegakure.ta.model.response.LogoutResponse
import com.kodegakure.ta.model.response.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    lateinit var name: TextView
    private lateinit var nip: TextView
    private lateinit var email: TextView
    private lateinit var nomorHp: TextView
    private lateinit var alamat: TextView
    private lateinit var tempatTanggalLahir: TextView

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
        val ifl = inflater.inflate(R.layout.fragment_profile, container, false)
        val buttonLogout = ifl.findViewById<Button>(R.id.buttonLogout)

        name = ifl.findViewById(R.id.textViewName)
        nip = ifl.findViewById(R.id.textViewNip)
        email = ifl.findViewById(R.id.textViewEmail)
        nomorHp = ifl.findViewById(R.id.textViewNomorHp)
        alamat = ifl.findViewById(R.id.textViewAlamat)
        tempatTanggalLahir = ifl.findViewById(R.id.textViewTempatTangggalLahir)

        getUserProfile()
        buttonLogout.setOnClickListener {
            logout()
        }
        return ifl
    }

    private fun getUserProfile() {
        NetworkConfigurations().getService().userProfile(token = "Bearer ${getToken()}")
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()!!
                        name.text = res.name
                        nip.text = res.nip
                        email.text = res.email
                        nomorHp.text = res.phone_number
                        alamat.text = res.address
                        tempatTanggalLahir.text =
                            getString(R.string.tempat_tanggal_lahir, res.birthplace, res.birthdate)
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("failed", t.message.toString())
                }

            })
    }

    private fun logout() {
        val sp = this.requireActivity().getSharedPreferences("auth", 0)
        val editor = sp.edit()

        NetworkConfigurations().getService().logout(token = "Bearer ${getToken()}")
            .enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>
                ) {
                    editor.remove("token")
                    editor.remove("userName")
                    editor.apply()
                    Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                override fun onFailure(
                    call: Call<LogoutResponse>,
                    t: Throwable
                ) {
                    Log.e("failed", t.message.toString())
                }

            })
    }

    private fun getToken(): String? {
        val sp = this.requireActivity().getSharedPreferences("auth", 0)
        return sp.getString("token", "")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}