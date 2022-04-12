package com.example.flo
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson


class SongActivity : AppCompatActivity()   {

    lateinit var binding: ActivitySongBinding
    lateinit var song : Song  // Data class
    lateinit var timer : Timer  // Timer thread
    private var mediaPlayer : MediaPlayer? = null  // 노래 재생
    private var gson : Gson = Gson()  // Json 변환

    override fun onCreate(savedInstanceState: Bundle?) {  // SongActivity 최초 실행 시 일어날 작업
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)  // SongActivity를 보여줌
        setContentView(binding.root)

        initSong()  // main에서 값 받아오기

        binding.songDownIb.setOnClickListener {  // 다시 mainActivity로 돌아감
            finish()
        }

        // 재생, 일시정지 버튼 관리
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        // 기본재생, 전체재생, 한곡재생 관리
        binding.songRepeatOffIv.setOnClickListener {
            setRepeatStatus(true)
        }
        binding.songRepeatOnIv.setOnClickListener {
            restartTimer()  // 한곡재생 버튼을 눌렀을 때는 thread 재시작 해주기
            setRepeatOneStatus(true)
        }
        binding.songRepeatOnOneIv.setOnClickListener {
            setRepeatOneStatus(false)
        }

        // 기본재생, 랜덤재생 관리
        binding.songRandomOffIv.setOnClickListener {
            setRandomStatus(true)
        }
        binding.songRandomOnIv.setOnClickListener {
            setRandomStatus(false)
        }
    }

    override fun onStart() {  // 다시 SongActivity로 돌아왔을 때 할 작업
        super.onStart()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)  // SongActivity에서 저장한 song을 불러옴
        val songJson = sharedPreferences.getString("songData", null)  // song 내부의 data를 의미

        song = if(songJson == null){  // 처음에는 data가 없기 때문에 오류를 막기 위해 null일 때도 작성
            Song("라일락","아이유(IU)", 0, 60, false, "music_lilac")
        } else{
            gson.fromJson(songJson, Song::class.java)
        }

        startTimer()
        setPlayer(song)
    }

    // 사용자가 focus를 잃었을 때 음악이 중지 (다른 activity가 실행되거나 앱이 잠시 background로 갔을 때)
    override fun onPause() {
        super.onPause()
        timer.interrupt()

        song.second = ((binding.songProgressSb.progress * song.playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌
        val songJson = gson.toJson(song)  // Json 객체 생성
        editor.putString("songData", songJson)

        editor.apply()  // 내부 저장소에 값 저장
    }

    override fun onStop() {  // Acitivy가 아예 전환된 후 사용하지 않는 resource 해제
        super.onStop()

        mediaPlayer?.stop()
    }

    override fun onDestroy() {  // 앱이 아예 종료됐을 때
        super.onDestroy()
        timer.interrupt()  // thread 종료
        mediaPlayer?.release()  // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null  // 미디어 플레이어 해제
    }

    private fun initSong(){  // mainActivity에서 값 받아오기
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime",0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }
    }

    private fun setPlayer(song : Song){  // 받아온 값 적용
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSignerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 100000)/song.playTime

        var music = resources.getIdentifier(song.music, "raw", this.packageName)  // MediaPlayer 생성
        mediaPlayer = MediaPlayer.create(this, music)

        mediaPlayer?.seekTo(binding.songProgressSb.progress)  // seekbar 위치부터 재생

        setPlayerStatus(song.isPlaying)  // song의 Boolean 값을 따름
    }

    private fun setPlayerStatus(isPlaying : Boolean) {  // 재생, 일지정지 관리
        song.isPlaying = isPlaying  // 재생 값에 따라 Song의 값도 바꿔줌
        timer.isPlaying = isPlaying  // 타이머도 동기화

        if(isPlaying) {  // 재생 상태 (일지정지 버튼을 보여줌)
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else{  // 정지 상태 (재생 버튼을 보여줌)
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }
    }

    // 기본재생, 전체재생, 한곡재생 관리
    private fun setRepeatStatus(isOn : Boolean) {
        if(isOn) {  // 전체재생 상태
            binding.songRepeatOnIv.visibility = View.VISIBLE
            binding.songRepeatOffIv.visibility = View.GONE
        }
    }

    private fun setRepeatOneStatus(isOn : Boolean) {
        if(isOn) {  // 반복재생 상태
            binding.songRepeatOnIv.visibility = View.GONE
            binding.songRepeatOnOneIv.visibility = View.VISIBLE
        }
        else {  // 기본재생 상태
            binding.songRepeatOnOneIv.visibility = View.GONE
            binding.songRepeatOffIv.visibility = View.VISIBLE
        }
    }

    private fun setRandomStatus(isOn : Boolean) {  // 랜덤재생 관리
        if(isOn) {  // 랜덤재생 상태
            binding.songRandomOffIv.visibility = View.GONE
            binding.songRandomOnIv.visibility = View.VISIBLE
        }
        else {  // 기본재생 상태
            binding.songRandomOffIv.visibility = View.VISIBLE
            binding.songRandomOnIv.visibility = View.GONE
        }
    }

    private fun startTimer(){  // 타이머 thread 시작
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    private fun restartTimer(){  // 타이머 thread 재시작
        timer.interrupt()  // 이전 thread를 종료
        mediaPlayer?.reset()  // 노래도 시작으로 되돌아감
        song.second = 0
        song.isPlaying = false  // 재생 상태도 일지정지 상태로 설정
        startTimer()  // 재시작
        setPlayer(song)
    }

    inner class Timer(private val playTime : Int, var isPlaying : Boolean = true) : Thread() {  // 타이머 thread
        private var second : Int = song.second
        private var mills : Float = second.toFloat() * 1000

        override fun run() {
            super.run()
            try{
                while(true){
                    if(second >= playTime){  // 시간이 다 되면 종료
                        mediaPlayer?.reset()
                        setPlayerStatus(false)
                        break
                    }

                    if(isPlaying){  // 재생상태일 때
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()  // seekbar 이동
                        }

                        if(mills % 1000 == 0f){
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)  // 시간도 계속 바꿔줌
                            }
                            second++
                        }
                    }
                }
            }
            catch (e : InterruptedException){  // interrupt 대비
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }

        }
    }

}