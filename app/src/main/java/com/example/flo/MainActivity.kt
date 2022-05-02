package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var mediaPlayer : MediaPlayer? = null
    lateinit var progress : Progress
    private var song : Song = Song()
    private var gson : Gson = Gson()

    var progressHandler = Handler(Looper.getMainLooper()) {  // progress bar를 업데이트하기 위한 핸들러
        progressBar()
        true
    }

    var resetHandler = Handler(Looper.getMainLooper()){  // 재생이 끝나면 다시 처음으로 되돌아가기 위한 핸들러
        binding.mainProgressSb.progress = 0  // 초기화
        song.current = 0
        song.second = 0

        mediaPlayer?.reset()
        setPlayerStatus(false)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)  // MediaPlayer 다시 생성
        mediaPlayer = MediaPlayer.create(this, music)

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
            intent.putExtra("order", song.order)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("albumImg", song.albumImg)
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

        inputDummySongs()

        initBottomNavigation()

        binding.mainProgressSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar : SeekBar?) {  // 터치 시작

            }

            override fun onProgressChanged(seekBar : SeekBar?, progress : Int, fromUser : Boolean) {  // 터치 중
                if(fromUser){  // 사용자가 클릭하고 있을 때만 값 변경
                    binding.mainProgressSb.progress = seekBar!!.progress
                }
            }

            override fun onStopTrackingTouch(seekBar : SeekBar?) {  // 터치 끝
                // 변경된 값을 받아옴
                binding.mainProgressSb.progress = seekBar!!.progress
                song.second = (seekBar.progress * song.playTime) / 100000
                mediaPlayer?.seekTo(binding.mainProgressSb.progress)

                // 변경된 값을 UI에 적용
                progress.interrupt()
                startProgress()

                Log.d("progress 변경 완료", seekBar.progress.toString())
            }
        })

        Log.d("Song", song.title + song.singer)

    }

    override fun onStart() {  // 다시 MainActivity로 돌아왔을 때 할 작업
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)  // SongActivity에서 저장한 song을 불러옴
        val songJson = sharedPreferences.getString("songData", null)  // song 내부의 data를 의미

        song = if(songJson == null){  // 처음에는 data가 없기 때문에 오류를 막기 위해 null일 때도 작성
            Song("01","라일락","아이유(IU)", R.drawable.img_album_exp2,0, 215, false, "music_lilac")
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

//    override fun onStop() {  // Acitivy가 아예 전환된 후 사용하지 않는 resource 해제
//        super.onStop()
//
//    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()  // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null  // 미디어 플레이어 해제

        progress.interrupt()
        //song.second = 0
        song.isPlaying = false

        // MainActivity, SongActivity의 데이터를 저장하고 종료
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

        val music = resources.getIdentifier(song.music, "raw", this.packageName)  // MediaPlayer 생성
        mediaPlayer = MediaPlayer.create(this, music)

        // SongActivity와 재생 상태 동기화
        mediaPlayer?.seekTo(song.current)
        setPlayerStatus(song.isPlaying)
    }

    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "01",
                "TOMBOY",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "01",
                "라일락",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "01",
                "Next Level",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3,
                0,
                220,
                false,
                "music_tomboy",
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "01",
                "Boy with Luv",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4,
                0,
                220,
                false,
                "music_tomboy",
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "01",
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                R.drawable.img_album_exp5,
                0,
                210,
                false,
                "music_tomboy",
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "01",
                "Weekend",
                "태연 (Tae Yeon)",
                R.drawable.img_album_exp6,
                0,
                230,
                false,
                "music_tomboy",
                0,
                false
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    fun changeSong() {  // 다른 fragment에서 음악을 바꿀 때 사용할 함수
        mediaPlayer?.reset()  // 음악 멈춤
        progress.interrupt()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)  // 새로운 노래 정보 다운로드
        val songJson = sharedPreferences.getString("songData", null)
        song = gson.fromJson(songJson, Song::class.java)

        // 음악 초기화
        startProgress()
        progressBar()
        setMiniPlayer(song)
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
                        resetHandler.sendEmptyMessage(1)
                        break
                    }

                    if(isPlaying){
                        sleep(50)
                        mills += 50

                        if(mills % 1000 == 0f){
                            second++
                        }
                        progressHandler.sendEmptyMessage(0)
                    }
                }
            }
            catch (e : InterruptedException){
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}