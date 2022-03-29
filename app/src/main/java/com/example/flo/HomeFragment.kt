package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.com.example.flo.AlbumFragment
import com.example.flo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeTodayMusicAlbum01Iv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm,
                AlbumFragment()
            ).commitAllowingStateLoss()
        }

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(Banner2Fragment())
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        var homePannelAdapter = HomepannerVPAdapter(this)
        homePannelAdapter.addFragment(Homepannel01Fragment())
        homePannelAdapter.addFragment(Homepannel02Fragment())
        homePannelAdapter.addFragment(Homepannel03Fragment())
        binding.homePannel01Vp.adapter = homePannelAdapter
        binding.homePannel01Vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }
}