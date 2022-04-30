package com.example.flo
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class AlbumFragment : Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private var gson : Gson = Gson()
    private val information = arrayListOf("수록곡", "상세정보", "영상")  // Tab에 들어갈 내용

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson = arguments?.getString("album")  // albumList[position]에서 받아온 값
        val album = gson.fromJson(albumJson, Album::class.java)
        setInit(album)

        // SongFragment에 data 전달
        val sharedPreferences = requireActivity().getSharedPreferences("songs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        val songsJson = gson.toJson(album)  // Json 객체 생성
        editor.putString("songsData", songsJson)

        editor.apply()  // 내부 저장소에 값 저장

        binding.albumBackIv.setOnClickListener {  // 다시 HomeFragment로 돌아감
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(
                R.id.main_frm,
                HomeFragment()
            ).commitAllowingStateLoss()
        }

        // ViewPager를 위한 어뎁터
        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter
        // ViewPager와 TabLayout 연결
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp){
                tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    private fun setInit(album : Album){  // album 정보 update
        binding.albumAlbumIv.setImageResource((album.coverImg!!))
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()
    }

}
