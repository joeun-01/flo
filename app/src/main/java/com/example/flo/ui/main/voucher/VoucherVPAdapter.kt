package com.example.flo.ui.main.voucher

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.ui.main.locker.SongfileFragment

class VoucherVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> VoucherBuyFragment()
            else -> MyVoucherFragment()
        }
    }
}