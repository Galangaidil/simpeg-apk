package com.kodegakure.ta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val attendanceFragment = AttendanceFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        setCurrentFragment(homeFragment)

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.attendances->setCurrentFragment(attendanceFragment)
                R.id.profile->setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.flFragment,fragment)
        commit()
    }
}
