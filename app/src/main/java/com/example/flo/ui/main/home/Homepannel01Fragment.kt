package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentHomePannel01Binding

class Homepannel01Fragment : Fragment() {
    lateinit var binding: FragmentHomePannel01Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePannel01Binding.inflate(inflater, container, false)

        return binding.root
    }

}