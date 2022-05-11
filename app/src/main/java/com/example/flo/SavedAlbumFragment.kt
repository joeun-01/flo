package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSavedAlbumBinding
import java.text.Bidi

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

        savedAlbumRVAdapter.setMyItemClickListener(object : SavedAlbumRVAdapter.MyItemClickListener {
            override fun onRemoveSong(albumId: Int) {
                albumDB.albumDao().dislikedAlbum(getJwt(), albumId)
            }
        })

        savedAlbumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(getJwt()) as ArrayList)
    }

    private fun getJwt() : Int {
        val sharedPreferences = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

        return sharedPreferences!!.getInt("jwt", 0)  // jwt 값이 없으면 0을 반환
    }

}