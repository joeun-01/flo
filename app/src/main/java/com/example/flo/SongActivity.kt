package com.example.flo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity()   {

    lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songRepeatOffIv.setOnClickListener {
            setRepeatStatus(false)
        }
        binding.songRepeatOnIv.setOnClickListener {
            setRepeatOneStatus(false)
        }
        binding.songRepeatOnOneIv.setOnClickListener {
            setRepeatOneStatus(true)
        }

        binding.songRandomOffIv.setOnClickListener {
            setRandomStatus(false)
        }
        binding.songRandomOnIv.setOnClickListener {
            setRandomStatus(true)
        }

        if(intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSignerNameTv.text = intent.getStringExtra("singer")
        }
    }

    fun setPlayerStatus(isPlaying : Boolean) {
        if(isPlaying) {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
        else{
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }

    fun setRepeatStatus(isOn : Boolean) {
        if(!isOn) {
            binding.songRepeatOnIv.visibility = View.VISIBLE
            binding.songRepeatOffIv.visibility = View.GONE
        }
    }

    fun setRepeatOneStatus(isOn : Boolean) {
        if(isOn) {
            binding.songRepeatOnOneIv.visibility = View.GONE
            binding.songRepeatOffIv.visibility = View.VISIBLE
        }
        else {
            binding.songRepeatOnIv.visibility = View.GONE
            binding.songRepeatOnOneIv.visibility = View.VISIBLE
        }
    }

    fun setRandomStatus(isOn : Boolean) {
        if(isOn) {
            binding.songRandomOffIv.visibility = View.VISIBLE
            binding.songRandomOnIv.visibility = View.GONE
        }
        else {
            binding.songRandomOffIv.visibility = View.GONE
            binding.songRandomOnIv.visibility = View.VISIBLE
        }
    }
}