package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var slide : AutoSlide
    private var position : Int = 0
    private var albumDatas = ArrayList<Album>()

    val handler = Handler(Looper.getMainLooper()){
        setPage()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        binding.homeTodayMusicAlbum01Iv.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm,
//                AlbumFragment()
//            ).commitAllowingStateLoss()
//        }

        albumDatas.apply {
            add(Album("TOMBOY", "(여자)아이들", R.drawable.img_album_exp13))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
        }

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(Banner2Fragment())
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val homePannelAdapter = HomepannerVPAdapter(this)
        homePannelAdapter.addFragment(Homepannel01Fragment())
        homePannelAdapter.addFragment(Homepannel02Fragment())
        homePannelAdapter.addFragment(Homepannel03Fragment())
        binding.homePannelVp.adapter = homePannelAdapter
        binding.homePannelVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.homePannelTb, binding.homePannelVp) {
            tab, position ->
        }.attach()

        slide = AutoSlide()
        slide.start()

        return binding.root
    }

    private fun setPage(){
        if(position == 3){
            position = 0
        }
        binding.homePannelVp.setCurrentItem(position, false)
        position++
    }

    inner class AutoSlide : Thread(){
        override fun run() {
            while (true){
                try {
                    Thread.sleep(2000)
                    handler.sendEmptyMessage(0)
                }
                catch(e : InterruptedException){
                    Log.d("자동 슬라이드", "interrupt 발생")
                }
            }
        }
    }

}