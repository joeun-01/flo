package com.example.flo.ui.main.look

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.flo.remote.LookView
import com.example.flo.databinding.FragmentLookBinding
import com.example.flo.remote.FloChartResult
import com.example.flo.remote.SongService

class LookFragment : Fragment(), LookView {

    lateinit var binding: FragmentLookBinding
    lateinit var floChartAdapter : ChartRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        binding.lookChipTitle02Tv.setOnClickListener {
            binding.lookSv.scrollTo(0, binding.lookSubTitleGenreTv.top)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getSongs()
    }

    private fun initRecyclerView(result: FloChartResult) {
        floChartAdapter = ChartRVAdapter(requireContext(), result)

        binding.lookFloChartRv.adapter = floChartAdapter
    }

    private fun getSongs() {  // song data를 가져옴
        val songService = SongService()

        songService.setLookView(this)
        songService.getSongs()
    }

    override fun onGetSongLoading() {
        binding.lookLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetSongSuccess(code: Int, result: FloChartResult) {
        binding.lookLoadingPb.visibility = View.GONE
        initRecyclerView(result)  // 연결에 성공하면 RecyclerView에 data를 넣어줌
    }

    override fun onGetSongFailure(code: Int, message: String) {
        binding.lookLoadingPb.visibility = View.GONE
        Log.d("LOOK_FRAG/SONG-RESPONSE", message)
    }
}