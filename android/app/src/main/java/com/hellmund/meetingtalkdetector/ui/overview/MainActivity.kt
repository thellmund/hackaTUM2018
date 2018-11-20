package com.hellmund.meetingtalkdetector.ui.overview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hellmund.meetingtalkdetector.R
import com.hellmund.meetingtalkdetector.ui.shared.BaseFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, OverviewFragment.newInstance())
                .commit()
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as? BaseFragment
            fragment?.onBackPressed()
        }
    }

}
