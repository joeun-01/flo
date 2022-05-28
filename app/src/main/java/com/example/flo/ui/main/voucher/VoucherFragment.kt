package com.example.flo.ui.main.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.R
import com.example.flo.databinding.FragmentVoucherBinding
import com.example.flo.ui.main.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator

class VoucherFragment : Fragment() {

    lateinit var binding : FragmentVoucherBinding

    private var tab = arrayListOf("이용권", "MY")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoucherBinding.inflate(inflater, container, false)

        binding.voucherBackIv.setOnClickListener {  // 다시 HomeFragment로 돌아감
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).addToBackStack(null).commit()
        }

        // ViewPager 연결
        val voucherVPAdapter = VoucherVPAdapter(this)
        binding.voucherVoucherVp.adapter = voucherVPAdapter

        // ViewPager와 TabLayout 연결
        TabLayoutMediator(binding.voucherVoucherTb, binding.voucherVoucherVp) {
            tab, position ->
            tab.text = this.tab[position]
        }.attach()

        return binding.root
    }

}