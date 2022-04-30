package com.example.flo

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val gson : Gson = Gson()
    private lateinit var slide : AutoSlide
    private var position : Int = 0
    private var albumDatas = ArrayList<Album>()
    private var songsTomboy = ArrayList<Song>()
    private var songsLilac = ArrayList<Song>()

    val handler = Handler(Looper.getMainLooper()){
        setPage()  // message를 받으면 page를 넘김
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        songsTomboy.apply{
            add(Song("01", "TOMBOY", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("02","말리지 마", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("03","VILLAIN DIES", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("04","ALREADY", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("05","POLAROID", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("06","ESCAPE", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("07","LIAR", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
            add(Song("08","MY BAG", "(여자)아이들", R.drawable.img_album_exp13, 0, 60, false, "music_lilac"))
        }

        songsLilac.apply{
            add(Song("01", "라일락", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("02","Flu", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("03","Coin", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("04","봄 안녕 봄", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("05","Celebrity", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("06","돌림노래 (Feat. DEAN)", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("07","빈 컵 (Empty Cup)", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("08","아이와 나의 바다", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("09","어푸 (Ah puh)", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
            add(Song("10","에필로그", "아이유 (IU)", R.drawable.img_album_exp2, 0, 60, false, "music_lilac"))
        }

        albumDatas.apply {  // recycler view를 위한 더미데이터
            add(Album("TOMBOY", "(여자)아이들", R.drawable.img_album_exp13, songsTomboy))
            add(Album("LILAC", "아이유 (IU)", R.drawable.img_album_exp2, songsLilac))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3, songsTomboy))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4, songsLilac))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, songsTomboy))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6, songsLilac))
        }

        // RecyclerView 어뎁터 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onPlayAlbum(album: Album) {
                playAlbum(album.songs)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
        })

        // 여기에서 albumJson으로 값을 저장함


        // 배너 ViewPager 어뎁터 연결
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(Banner2Fragment())
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 패널 ViewPager 어뎁터 연결
        val homePannelAdapter = HomepannerVPAdapter(this)
        homePannelAdapter.addFragment(Homepannel01Fragment())  // 패널 fragment 추가
        homePannelAdapter.addFragment(Homepannel02Fragment())
        homePannelAdapter.addFragment(Homepannel03Fragment())
        binding.homePannelVp.adapter = homePannelAdapter
        binding.homePannelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.homePannelTb, binding.homePannelVp) {  // ViewPager와 TabLayout 연결
            tab, position ->  // Tab
        }.attach()

        slide = AutoSlide()  // ViewPager thread 시작
        slide.start()

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {  // fragment를 전환하면서 Album 데이터를 넘겨줌
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    private fun playAlbum(songs : ArrayList<Song>?){
        val sharedPreferences = requireActivity().getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        val songJson = gson.toJson(songs!![0])  // Json 객체 생성
        editor.putString("songData", songJson)

        editor.apply()  // 내부 저장소에 값 저장

        (activity as MainActivity).
//        Toast.makeText(activity, "$songJson", Toast.LENGTH_LONG).show()
    }

    private fun setPage(){  // page를 넘겨주는 함수
        if(position == 3){
            position = 0
        }
        binding.homePannelVp.setCurrentItem(position, false)
        position++
    }

    inner class AutoSlide : Thread(){  // 자동 슬라이드를 위한 thread
        override fun run() {
            while (true){
                try {
                    sleep(2000)
                    handler.sendEmptyMessage(0)  // handler에 message를 보냄
                }
                catch(e : InterruptedException){
                    Log.d("자동 슬라이드", "interrupt 발생")
                }
            }
        }
    }

}