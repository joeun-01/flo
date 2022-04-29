package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSongBinding

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

        // 여기에서 albumJson을 불러옴
        // albumJson의 songs값을 받아옴
        // 받아와서 recyclerView에 넣어줌


//        // RecyclerView 어뎁터 연결
//        val songRVAdapter = SongRVAdapter(songs)
//        binding.albumSongsListRv.adapter = songRVAdapter
//        // LayoutManager를 통해 Layout 설정
//        binding.albumSongsListRv.layoutManager = LinearLayoutManager(context,
//            LinearLayoutManager.VERTICAL, false)
//
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