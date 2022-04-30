package com.example.flo

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSongBinding
import com.google.gson.Gson

class SongFragment : Fragment() {
    lateinit var binding :FragmentSongBinding
    private var gson : Gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater,container,false)

        // activity에서 sharedPreferences를 불러와서 데이터 저장
        val sharedPreferences = requireActivity().getSharedPreferences("songs", MODE_PRIVATE)
        val songsJson = sharedPreferences.getString("songsData", null)
        val songs = gson.fromJson(songsJson, Album::class.java)

        binding.albumSongsToggleOffIv.setOnClickListener {  // toggle 켜기
            setToggleStatus(false)
        }
        binding.albumSongsToggleOnIv.setOnClickListener {  // toggle 끄기
            setToggleStatus(true)
        }

//        binding.songLilacLayout.setOnClickListener {
//            Toast.makeText(activity,"LILAC", Toast.LENGTH_SHORT).show()
//        }

        // 여기에서 albumJson을 불러옴
        // albumJson의 songs값을 받아옴
        // 받아와서 recyclerView에 넣어줌

        try {
            val songRVAdapter = SongRVAdapter(songs.songs)
            binding.albumSongsListRv.adapter = songRVAdapter
            binding.albumSongsListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        catch (e: NullPointerException){
            Log.d("recyclerView", "null")
        }


//        songRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener{
//            override fun onItemClick(song: Song) {
//
//            }
//        })

        return binding.root
    }

    private fun setToggleStatus(isOn : Boolean){
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