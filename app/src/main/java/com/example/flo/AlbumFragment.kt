package com.example.flo
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class AlbumFragment : Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private var gson : Gson = Gson()
    private val information = arrayListOf("수록곡", "상세정보", "영상")  // Tab에 들어갈 내용

    lateinit var songDB : SongDatabase
    lateinit var albumInfo : Album

    private var isLiked : Boolean= false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireActivity())!!

        var albumIdx = arguments?.getInt("albumIdx")  // album.id를 불러옴

        albumInfo = songDB.songDao().getAlbum(albumIdx)

        // 현재 앨범에 대한 데이터를 반영
        isLiked = isLikedAlbum(albumInfo.id)
        setInit(albumInfo)
        setOnLikeListener(albumInfo)

        // SongFragment에 album ID 전달
        val sharedPreferences = requireActivity().getSharedPreferences("album", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        editor.putInt("albumID", albumInfo.id)
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

        if(isLiked) {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }
        else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt() : Int {
        val sharedPreferences = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

        return sharedPreferences!!.getInt("jwt", 0)  // jwt 값이 없으면 0을 반환
    }

    private fun likeAlbum(userId : Int, albumId: Int) {
        val like = Like(userId, albumId)  // 좋아요를 누르면 like table을 업데이트

        songDB.albumDao().likeAlbum(like)
    }

    private fun isLikedAlbum(albumId: Int) : Boolean {  // 좋아요를 눌렀는지 확인
        val userId = getJwt()

        val likeId = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null  // 좋아요를 안하면 null -> false를 반환
    }

    private fun dislikedAlbum(albumId: Int) {  // 좋아요 삭제
        val userId = getJwt()

        songDB.albumDao().dislikedAlbum(userId, albumId)
    }

    private fun setOnLikeListener(album: Album) {
        val userId = getJwt()

        binding.albumLikeIv.setOnClickListener {
            if(isLiked) {  // 좋아요를 취소
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                dislikedAlbum(album.id)
            }
            else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
        }
    }
}
