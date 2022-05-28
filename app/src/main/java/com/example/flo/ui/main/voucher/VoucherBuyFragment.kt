package com.example.flo.ui.main.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentVoucherBuyBinding

class VoucherBuyFragment : Fragment() {

    lateinit var binding : FragmentVoucherBuyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = com.example.flo.databinding.FragmentVoucherBuyBinding.inflate(inflater, container, false)

        return binding.root
    }

}