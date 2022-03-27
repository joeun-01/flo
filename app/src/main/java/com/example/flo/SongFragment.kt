package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {
    lateinit var binding :FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater,container,false)

        binding.albumSongsToggleOffIv.setOnClickListener {
            setToggleStatus(false)
        }
        binding.albumSongsToggleOnIv.setOnClickListener {
            setToggleStatus(true)
        }


        binding.songLilacLayout.setOnClickListener {
            Toast.makeText(activity,"LILAC", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
    fun setToggleStatus(isOn : Boolean){
        if(isOn){
            binding.albumSongsToggleOnIv.visibility = View.GONE
            binding.albumSongsToggleOffIv.visibility = View.VISIBLE
        }
        else{
            binding.albumSongsToggleOnIv.visibility = View.VISIBLE
            binding.albumSongsToggleOffIv.visibility = View.GONE
        }
    }

}