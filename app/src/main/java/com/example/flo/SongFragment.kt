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
            Toast.makeText(activity, "$songs", Toast.LENGTH_LONG).show()
            setToggleStatus(false)
        }
        binding.albumSongsToggleOnIv.setOnClickListener {  // toggle 끄기
            setToggleStatus(true)
        }

        try {  // sharedPreferences에서 받아 온 data를 RecyclerView에 저장
            val songRVAdapter = SongRVAdapter(songs.songs)
            binding.albumSongsListRv.adapter = songRVAdapter
            binding.albumSongsListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            songRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener{
                override fun onPlayAlbum(song : Song) {
                    playAlbum(song)
                }
            })
        }
        catch (e: NullPointerException){
            Log.d("recyclerView", "null")
        }

        return binding.root
    }

    private fun playAlbum(song : Song){
        val sharedPreferences = requireActivity().getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        val songJson = gson.toJson(song)  // Json 객체 생성
        editor.putString("songData", songJson)

        editor.apply()  // 내부 저장소에 값 저장

        (activity as MainActivity).changeSong()
//        Toast.makeText(activity, "$song", Toast.LENGTH_LONG).show()
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