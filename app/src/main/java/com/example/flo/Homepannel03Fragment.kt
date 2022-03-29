package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentHomePannel03Binding

class Homepannel03Fragment : Fragment() {
    lateinit var binding : FragmentHomePannel03Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePannel03Binding.inflate(inflater, container, false)

        return binding.root
    }
}