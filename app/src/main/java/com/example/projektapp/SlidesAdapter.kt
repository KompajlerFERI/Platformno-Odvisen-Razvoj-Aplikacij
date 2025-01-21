package com.example.projektapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SlidesAdapter(fragmentActivity: FragmentActivity, private val data: Bundle) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DialogCapacityFragment().apply {
                arguments = data
            }
            else -> DialogMQTTFragment().apply {
                arguments = data
            }
        }
    }
}