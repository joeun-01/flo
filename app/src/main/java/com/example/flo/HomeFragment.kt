package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class HomeFragment : Fragment(), HomeView {

    lateinit var binding: FragmentHomeBinding
    private lateinit var slide : AutoSlide
    private var position : Int = 0

    lateinit var songDB : SongDatabase
    private val gson : Gson = Gson()

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

        songDB = SongDatabase.getInstance(requireActivity())!!

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

    override fun onStart() {
        super.onStart()
        getAlbums()
    }

    private fun initRecyclerView(result: AlbumResult) {

//        albumDatas.addAll(songDB.albumDao().getAlbums())

        // RecyclerView 어뎁터 연결
        val albumRVAdapter = AlbumRVAdapter(requireContext(), result)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Albums) {
                changeAlbumFragment(album)
            }

            override fun onPlayAlbum(albumID: Int) {  // albumID를 받아와서 앨범 첫 곡부터 전체재생
                playAlbum(albumID)
            }
//
//            override fun onRemoveAlbum(position: Int) {
//                albumRVAdapter.removeItem(position)
//            }
        })
    }

    private fun getAlbums() {  // song data를 가져옴
        val albumService = AlbumService()

        albumService.setHomeView(this)
        albumService.getAlbums()
    }

    override fun onGetAlbumLoading() {
//        binding.lookLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetAlbumSuccess(code: Int, result: AlbumResult) {
//        binding.lookLoadingPb.visibility = View.GONE
        initRecyclerView(result)  // 연결에 성공하면 RecyclerView에 data를 넣어줌
    }

    override fun onGetAlbumFailure(code: Int, message: String) {
//        binding.lookLoadingPb.visibility = View.GONE
        Log.d("LOOK_FRAG/SONG-RESPONSE", message)
    }

    private fun changeAlbumFragment(album: Albums) {  // fragment를 전환하면서 Album 데이터를 넘겨줌
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    private fun playAlbum(albumID : Int) {  // albumID를 이용하여 앨범 전체재생
        (activity as MainActivity).playAlbum(albumID)
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