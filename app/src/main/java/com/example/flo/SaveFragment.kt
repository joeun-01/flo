package com.example.flo

import android.os.Bundle
import android.view.CollapsibleActionView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSaveBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class SaveFragment : Fragment() {
    lateinit var binding: FragmentSaveBinding
    lateinit var songDB : SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater,container,false)

        songDB = SongDatabase.getInstance(requireContext())!!

        var isSelect = false  // 기본으로 전체선택을 안한 상태로 설정

        // BottomSheetDialog를 사용하기 위한 초기화
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetDialog.setCanceledOnTouchOutside(false)  // 외부 화면 터치 시 종료 여부


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

                bottomSheetDialog.show()

                isSelect = true
            }

        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // RecyclerView 어뎁터 연결
        val saveRVAdapter = SaveRVAdapter()
        binding.saveSongListRv.adapter = saveRVAdapter
        // LayoutManager를 통해 Layout 설정
        binding.saveSongListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        saveRVAdapter.addSongs(songDB.songDao().getLikeSongs(true) as ArrayList<Song>)

        saveRVAdapter.setMyItemClickListener(object : SaveRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

    }

}