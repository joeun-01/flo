package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var slide : AutoSlide
    private var position : Int = 0
    private var albumDatas = ArrayList<Album>()

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

        albumDatas.apply {  // recycler view를 위한 더미데이터
            add(Album("TOMBOY", "(여자)아이들", R.drawable.img_album_exp13))
            add(Album("LILAC", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
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

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
        })

        // 여기에서 SongAdapter를 선언하여 albumDatas의 songs<Song> 데이터를 통해서 SongFragment의 RecyclerView에 값을 채워줌
        // 아주 중요함 (context)로 지정하면 되지 않을까 하는 생각
        // 하지만 click event는 SongFragment에서 일어나기 때문에 SongFragment에서도 Adapter를 선언해줄 것
        // Adapter에서 view binding이 일어나기 때문에 꼭 SongFragment에서 DataList를 구성하지 않아도 되지 않을까?
        // 안되면 망함ㅎ


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
                    Thread.sleep(2000)
                    handler.sendEmptyMessage(0)  // handler에 message를 보냄
                }
                catch(e : InterruptedException){
                    Log.d("자동 슬라이드", "interrupt 발생")
                }
            }
        }
    }

}