package com.example.flo

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSongBinding
import com.google.gson.Gson

class SongFragment : Fragment() {
    lateinit var binding :FragmentSongBinding

    lateinit var songDB : SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater,container,false)

        songDB = SongDatabase.getInstance(requireActivity())!!

        // albumID 불러오기
        val sharedPreferences = requireActivity().getSharedPreferences("album", MODE_PRIVATE)
        val albumID = sharedPreferences.getInt("albumID", 0)

        binding.albumSongsToggleOffIv.setOnClickListener {  // toggle 켜기
            setToggleStatus(false)
        }
        binding.albumSongsToggleOnIv.setOnClickListener {  // toggle 끄기
            setToggleStatus(true)
        }

        binding.albumListenAllLy.setOnClickListener {
            (activity as MainActivity).playAlbum(albumID)
        }

        // recyclerView에 적용
        val songList = songDB.songDao().getSongsInAlbum(albumID)

        val songRVAdapter = SongRVAdapter(songList)
        binding.albumSongsListRv.adapter = songRVAdapter
        binding.albumSongsListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        songRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener{
            override fun onPlayAlbum(song : Song) {
                playAlbum(song)
            }
        })

        return binding.root
    }

    private fun playAlbum(song : Song){
        val sharedPreferences = requireActivity().getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌

        editor.putInt("songId", song.id)  // 내부 저장소에 값 저장
        editor.apply()

        (activity as MainActivity).changeSong()
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