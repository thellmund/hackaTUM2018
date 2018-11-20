package com.hellmund.meetingtalkdetector.ui.shared

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        val isHome = requireActivity().supportFragmentManager.backStackEntryCount == 0
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(isHome.not())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                requireActivity().supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onBackPressed() {
        requireActivity().supportFragmentManager.popBackStack()
    }

}
