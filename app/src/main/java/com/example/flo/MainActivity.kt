package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var mediaPlayer : MediaPlayer? = null
    lateinit var progress : Progress
    private var song : Song = Song()
    private var gson : Gson = Gson()

    var handler = Handler(Looper.getMainLooper()) {  // progress bar를 업데이트하기 위한 핸들러
        progressBar()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {  // 최초 실행 시 해야할 작업들
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_FLO)  // splash 화면이 끝나면 MainActivity 화면을 띄워주어야 함

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainPlayerCl.setOnClickListener {  // SongActivity로 전환
            //startActivity(Intent(this, SongActivity::class.java))
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)
            intent.putExtra("current", song.current)
            startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener {  // 정지
            setPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {  // 재생
            setPlayerStatus(false)
        }

        initBottomNavigation()

        Log.d("Song", song.title + song.singer)

    }

    override fun onStart() {  // 다시 MainActivity로 돌아왔을 때 할 작업
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)  // SongActivity에서 저장한 song을 불러옴
        val songJson = sharedPreferences.getString("songData", null)  // song 내부의 data를 의미

        song = if(songJson == null){  // 처음에는 data가 없기 때문에 오류를 막기 위해 null일 때도 작성
            Song("라일락","아이유(IU)", 0, 60, false, "music_lilac")
        } else{
            gson.fromJson(songJson, Song::class.java)
        }

        startProgress()
        progressBar()
        setMiniPlayer(song)
    }

    override fun onPause() {
        super.onPause()
        progress.interrupt()

        song.second = ((binding.mainProgressSb.progress * song.playTime)/100)/1000

        mediaPlayer?.stop()
        song.current = mediaPlayer!!.currentPosition

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        val songJson = gson.toJson(song)  // Json 객체 생성
        editor.putString("songData", songJson)

        editor.apply()  // 내부 저장소에 값 저장
    }

    override fun onStop() {  // Acitivy가 아예 전환된 후 사용하지 않는 resource 해제
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()  // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null  // 미디어 플레이어 해제

        progress.interrupt()
        //song.second = 0
        song.isPlaying = false


        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        val songJson = gson.toJson(song)  // Json 객체 생성
        editor.putString("songData", songJson)

        editor.apply()  // 내부 저장소에 값 저장
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {  // home 화면 실행
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {  // 검색 화면 실행
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {  // 보관함 화면 실행
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setPlayerStatus(isPlaying : Boolean){  // 재생 여부 관리
        song.isPlaying = isPlaying  // play 여부 동기화
        progress.isPlaying = isPlaying

        if(isPlaying){
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else{
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }
    }

    private fun setMiniPlayer(song : Song){  // miniPlayer 초기화
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second * 100000)/song.playTime

        var music = resources.getIdentifier(song.music, "raw", this.packageName)  // MediaPlayer 생성
        mediaPlayer = MediaPlayer.create(this, music)

        mediaPlayer?.seekTo(song.current)

        setPlayerStatus(song.isPlaying)
    }


    private fun startProgress(){  // Progress thread 시작작
       progress = Progress(song.playTime, song.isPlaying)
        progress.start()
    }

    private fun progressBar(){  // UI는 main Thread에서 변경
        binding.mainProgressSb.progress = ((progress.mills / song.playTime) * 100).toInt()
    }

    inner class Progress(private val playTime : Int, var isPlaying : Boolean) : Thread() {  // progressBar 변경 thread
        private var second : Int = song.second
        var mills : Float = second.toFloat() * 1000

            override fun run() {
            super.run()
            try{
                while(true){
                    if(second >= playTime){
                        mediaPlayer?.reset()
                        break
                    }

                    if(isPlaying){
                        sleep(50)
                        mills += 50

                        if(mills % 1000 == 0f){
                            second++
                        }
                        handler.sendEmptyMessage(0)
                    }
                }
            }
            catch (e : InterruptedException){
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}