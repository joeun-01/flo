package com.example.flo.ui.main.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.room.SongDatabase
import com.example.flo.databinding.FragmentSavedAlbumBinding

class SavedAlbumFragment : Fragment() {
    lateinit var binding: FragmentSavedAlbumBinding
    lateinit var albumDB : SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedAlbumBinding.inflate(inflater,container,false)

        albumDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // RecyclerView 어뎁터 연결
        val savedAlbumRVAdapter = SavedAlbumRVAdapter()
        binding.savedAlbumListRv.adapter = savedAlbumRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.savedAlbumListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savedAlbumRVAdapter.setMyItemClickListener(object :
            SavedAlbumRVAdapter.MyItemClickListener {
            override fun onRemoveSong(albumId: Int) {
                albumDB.albumDao().dislikedAlbum(getJwt(), albumId)
            }
        })

        savedAlbumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(getJwt()) as ArrayList)
    }

    private fun getJwt() : String? {
        val sharedPreferences = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)

        return sharedPreferences!!.getString("jwt", "")  // jwt 값이 없으면 0을 반환
    }

}