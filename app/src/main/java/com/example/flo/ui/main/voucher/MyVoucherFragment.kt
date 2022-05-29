package com.example.flo.ui.main.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentMyVoucherBinding

class MyVoucherFragment : Fragment() {

    lateinit var binding : FragmentMyVoucherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyVoucherBinding.inflate(inflater, container, false)


        return binding.root
    }

}