package com.example.flo

import android.os.Bundle
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
    private var songs = ArrayList<Song>()

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


//        binding.songLilacLayout.setOnClickListener {
//            Toast.makeText(activity,"LILAC", Toast.LENGTH_SHORT).show()
//        }

        songs.apply{
            add(Song("01", "TOMBOY", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("02","말리지 마", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("03","VILLAIN DIES", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("04","ALREADY", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("05","POLAROID", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("06","ESCAPE", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("07","LIAR", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("08","MY BAG", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
        }

        // RecyclerView 어뎁터 연결
        val songRVAdapter = SongRVAdapter(songs)
        binding.albumSongsListRv.adapter = songRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.albumSongsListRv.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)

        songRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener{
            override fun onItemClick(song: Song) {
                
            }
        })

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