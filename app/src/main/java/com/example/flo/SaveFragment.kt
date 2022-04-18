package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSaveBinding
import com.google.gson.Gson

class SaveFragment : Fragment() {
    lateinit var binding: FragmentSaveBinding
    private var locker = ArrayList<Locker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater,container,false)

        locker.apply {
            add(Locker("라일락", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Locker("TOMBOY", "(여자)아이들", R.drawable.img_album_exp13))
            add(Locker("LOVE DIVE", "IVE (아이브)", R.drawable.img_album_exp14))
            add(Locker("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Locker("드라마", "아이유 (IU)", R.drawable.img_album_exp15))
            add(Locker("Feel My Rhythm", "Red Velvet (레드벨벳)", R.drawable.img_album_exp16))
            add(Locker("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Locker("신호등", "이무진", R.drawable.img_album_exp17))
            add(Locker("내일이 오면", "릴보이 (feat.기리보이 & 서동현)", R.drawable.img_album_exp18))
        }

        // RecyclerView 어뎁터 연결
        val saveRVAdapter = SaveRVAdapter(locker)
        binding.saveSongListRv.adapter = saveRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.saveSongListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveRVAdapter.setMyItemClickListener(object : SaveRVAdapter.MyItemClickListener{
            override fun onRemoveAlbum(position: Int) {
                saveRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }


}