package com.example.flo.ui.main.album
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.flo.R
import com.example.flo.room.SongDatabase
import com.example.flo.data.entities.Like
import com.example.flo.databinding.FragmentAlbumBinding
import com.example.flo.remote.Albums
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.main.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class AlbumFragment : Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private val information = arrayListOf("수록곡", "상세정보", "영상")  // Tab에 들어갈 내용

    lateinit var songDB : SongDatabase
    private val gson : Gson = Gson()

    private var isLiked : Boolean= false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireActivity())!!

        var album = arguments?.getString("album")  // album.id를 불러옴
        var albumInfo = gson.fromJson(album, Albums::class.java)

//        albumInfo = songDB.songDao().getAlbum(albumIdx)

        // 현재 앨범에 대한 데이터를 반영
        isLiked = isLikedAlbum(albumInfo.albumIdx)
        setInit(albumInfo)
        setOnLikeListener(albumInfo)

        // SongFragment에 album ID 전달
        val sharedPreferences = requireActivity().getSharedPreferences("album", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        editor.putInt("albumID", albumInfo.albumIdx)
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

    private fun setInit(album : Albums){  // album 정보 update
        if(album.coverImgUrl == "" || album.coverImgUrl == null) {
            // url 값이 비어있지 않으면
        }
        else {  // url을 넣어줌
            Log.d("image", album.coverImgUrl)
            Glide.with(requireContext()).load(album.coverImgUrl).into(binding.albumAlbumIv)
        }

        binding.albumMusicTitleTv.text = album.title
        binding.albumSingerNameTv.text = album.singer

        if(isLiked) {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }
        else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt() : String? {
        val sharedPreferences = requireActivity().getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)

        return sharedPreferences!!.getString("jwt", "")  // jwt 값이 없으면 0을 반환
    }

    private fun likeAlbum(jwt : String, albumId: Int) {
        val like = Like(jwt, albumId)  // 좋아요를 누르면 like table을 업데이트

        songDB.albumDao().likeAlbum(like)
    }

    private fun isLikedAlbum(albumId: Int) : Boolean {  // 좋아요를 눌렀는지 확인
        val jwt = getJwt()

        val likeId = songDB.albumDao().isLikedAlbum(jwt, albumId)

        return likeId != null  // 좋아요를 안하면 null -> false를 반환
    }

    private fun dislikedAlbum(albumId: Int) {  // 좋아요 삭제
        val jwt = getJwt()

        songDB.albumDao().dislikedAlbum(jwt, albumId)
    }

    private fun setOnLikeListener(album: Albums) {
        val jwt = getJwt()

        binding.albumLikeIv.setOnClickListener {
            if(isLiked) {  // 좋아요를 취소
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                dislikedAlbum(album.albumIdx)
            }
            else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(jwt!!, album.albumIdx)
            }
        }
    }
}
