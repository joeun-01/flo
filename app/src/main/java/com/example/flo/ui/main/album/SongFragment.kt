package com.example.flo.ui.main.album

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.room.SongDatabase
import com.example.flo.remote.SongView
import com.example.flo.databinding.FragmentSongBinding
import com.example.flo.remote.AlbumService
import com.example.flo.remote.TrackResult
import com.example.flo.ui.main.MainActivity

class SongFragment : Fragment(), SongView {
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
        // val songList = songDB.songDao().getSongsInAlbum(albumID)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = requireActivity().getSharedPreferences("album", MODE_PRIVATE)
        val albumIdx = sharedPreferences.getInt("albumID", 0)

        getAlbumSongs(albumIdx)
    }

    private fun initRecyclerView(result: ArrayList<TrackResult>) {
        // RecyclerView 어뎁터 연결
        val songRVAdapter = SongRVAdapter(result)
        binding.albumSongsListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.albumSongsListRv.adapter = songRVAdapter


        songRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener {
            override fun onPlayAlbum(song : TrackResult) {
                playAlbumSongs(song)
            }
        })

    }

    private fun getAlbumSongs(albumIdx : Int) {
        val trackService = AlbumService()

        trackService.setSongView(this)
        trackService.getAlbumSongs(albumIdx)

    }

    override fun onGetTrackLoading() {
        Log.d("SONG_FRAG/SONG-RESPONSE", "로딩 중")
    }

    override fun onGetTrackSuccess(code: Int, result: ArrayList<TrackResult>) {
        Log.d("SONG_FRAG/SONG-RESPONSE", result.toString())
        initRecyclerView(result)
    }

    override fun onGetTrackFailure(code: Int, message: String) {
        Log.d("SONG_FRAG/SONG-RESPONSE", message)
    }

    private fun playAlbumSongs(song : TrackResult){
        val sharedPreferences = requireActivity().getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌

        editor.putInt("songId", song.songIdx)  // 내부 저장소에 값 저장
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