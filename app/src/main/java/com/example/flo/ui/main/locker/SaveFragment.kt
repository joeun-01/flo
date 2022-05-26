package com.example.flo.ui.main.locker

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.R
import com.example.flo.room.SongDatabase
import com.example.flo.data.entities.Song
import com.example.flo.databinding.FragmentSaveBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class SaveFragment : Fragment() {
    lateinit var binding: FragmentSaveBinding

    private var isSelect = false  // 선택안한 것을 기본으로

    lateinit var songDB : SongDatabase
    private var likedSongs = arrayListOf<Song>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater,container,false)

        songDB = SongDatabase.getInstance(requireContext())!!

        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(requireContext())

        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(false)

        binding.saveSelectLy.setOnClickListener {
            if(isSelect){  // 선택해제
                binding.saveSelectAllTv.visibility = View.VISIBLE
                binding.saveSelectOnTv.visibility = View.GONE

                binding.saveSelectAllIv.visibility = View.VISIBLE
                binding.saveSelectOnIv.visibility = View.GONE

                isSelect = false
            }
            else{  // 전체선택을 활성화
                binding.saveSelectAllTv.visibility = View.GONE
                binding.saveSelectOnTv.visibility = View.VISIBLE

                binding.saveSelectAllIv.visibility = View.GONE
                binding.saveSelectOnIv.visibility = View.VISIBLE

                isSelect = true

                dialog.show()
            }
        }

        val delete = dialogView.findViewById<LinearLayout>(R.id.bottomDialog_delete)

        dialog.setOnDismissListener {  // dialog가 꺼지면 다시 선택해제 상태로 돌아가도록
            binding.saveSelectAllTv.visibility = View.VISIBLE
            binding.saveSelectOnTv.visibility = View.GONE

            binding.saveSelectAllIv.visibility = View.VISIBLE
            binding.saveSelectOnIv.visibility = View.GONE

            isSelect = false
        }

        delete.setOnClickListener {
            // dialog를 종료하면서 좋아요한 곡들 삭제해줌
            deleteLikedSongs()
            initRecyclerView()

            dialog.dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        likedSongs.addAll(songDB.songDao().getLikeSongs(true))

        // RecyclerView 어뎁터 연결
        val saveRVAdapter = SaveRVAdapter()
        binding.saveSongListRv.adapter = saveRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.saveSongListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveRVAdapter.setMyItemClickListener(object : SaveRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        saveRVAdapter.addSongs(songDB.songDao().getLikeSongs(true) as ArrayList<Song>)
    }

    private fun deleteLikedSongs() {  // 좋아요한 list에 있는 곡들을 모두 취소
        for(i in 0 until likedSongs.size) {
            songDB.songDao().updateIsLikeById(false, likedSongs[i].id)
        }

        likedSongs.clear()
    }
}