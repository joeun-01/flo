package com.example.flo.ui.main.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.R
import com.example.flo.databinding.FragmentMyVoucherBinding
import com.example.flo.ui.main.MainActivity

class MyVoucherFragment : Fragment() {

    lateinit var binding : FragmentMyVoucherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyVoucherBinding.inflate(inflater, container, false)

        // 언젠가 이용권 구매를 누르면 voucher fragment로 넘어가도록 구현할것 ...........

        return binding.root
    }

}