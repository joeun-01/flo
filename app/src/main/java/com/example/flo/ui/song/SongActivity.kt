package com.example.flo.ui.song

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.R
import com.example.flo.room.SongDatabase
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivitySongBinding
import com.example.flo.databinding.ToastLikeBinding

class SongActivity : AppCompatActivity()   {
    // push 되는 지 확인

    lateinit var binding: ActivitySongBinding
    lateinit var toastBinding : ToastLikeBinding
    lateinit var timer : Timer  // Timer thread
    private var mediaPlayer : MediaPlayer? = null  // 노래 재생

    lateinit var songDB : SongDatabase  // 데이터베이스의 song 목록을 가져와서 songs에 저장
    val songs = arrayListOf<Song>()

    var nowPos = 0
    private var repeat = false

    var progressHandler = Handler(Looper.getMainLooper()) {
        progressUI()
        true
    }

    var timerHandler = Handler(Looper.getMainLooper()) {  // progress bar를 업데이트하기 위한 핸들러
        timerUI()
        true
    }

    var resetHandler = Handler(Looper.getMainLooper()){  // 재생이 끝나면 다시 처음으로 되돌아가기 위한 핸들러
        if(repeat) {  // 반복재생 상태일 때
            repeatSong()
        }
        else {  // 아닐 때
            moveSong(+1)
        }

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {  // SongActivity 최초 실행 시 일어날 작업
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)  // SongActivity를 보여줌
        toastBinding = ToastLikeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        songDB = SongDatabase.getInstance(this)!!

        initClickListener()
        initPlayList()

        binding.songProgressSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar : SeekBar?) {  // 터치 시작
                Log.d("현재 재생 위치", mediaPlayer!!.currentPosition.toString())
            }

            override fun onProgressChanged(seekBar : SeekBar?, progress : Int, fromUser : Boolean) {  // 터치 중
                if(fromUser){  // 사용자가 클릭하고 있을 때만 값 변경
                    binding.songProgressSb.progress = seekBar!!.progress
                }
            }

            override fun onStopTrackingTouch(seekBar : SeekBar?) {  // 터치 끝
                // 변경된 값을 받아옴
                binding.songProgressSb.progress = seekBar!!.progress
                songs[nowPos].second = (seekBar.progress * songs[nowPos].playTime) / 100000

                val ratio = mediaPlayer!!.duration / binding.songProgressSb.max

                if(binding.songProgressSb.progress == 0) {  // divideByZero 예외 처리
                    mediaPlayer?.seekTo(0)
                }
                else {
                    mediaPlayer?.seekTo(binding.songProgressSb.progress * ratio)
                }

                Log.d("바뀐 재생 위치", (binding.songProgressSb.progress * ratio).toString())
                Log.d("mediaPlayer 최대 값", mediaPlayer!!.duration.toString())

                // 변경된 값을 UI에 적용
                timer.interrupt()
                startTimer()
                binding.songStartTimeTv.text = String.format("%02d:%02d", songs[nowPos].second / 60, songs[nowPos].second % 60)

                Log.d("progress 변경 완료", seekBar.progress.toString())
            }
        })
    }

    override fun onStart() {  // 다시 SongActivity로 돌아왔을 때 할 작업
        super.onStart()

        val repeatSP = getSharedPreferences("repeat", MODE_PRIVATE)
        repeat = repeatSP.getBoolean("songRepeat", false)

        initSong()  // main에서 실행 중이던 곡 정보 받아오기

        startTimer()
        setPlayer(songs[nowPos])

        Log.d("받은 song 값", songs[nowPos].toString())
    }

    // 사용자가 focus를 잃었을 때 음악이 중지 (다른 activity가 실행되거나 앱이 잠시 background로 갔을 때)
    override fun onPause() {
        super.onPause()
        timer.interrupt()

        songDB.songDao().updateSecondById(((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000, songs[nowPos].id)
        songDB.songDao().updateIsPlayingById(songs[nowPos].isPlaying, songs[nowPos].id)

        mediaPlayer?.stop()
        songDB.songDao().updateCurrentById(mediaPlayer!!.currentPosition, songs[nowPos].id)

        // 재생할 song ID를 넘겨줌
        val songSP = getSharedPreferences("song", MODE_PRIVATE)
        val songEditor = songSP.edit()  // 에디터를 통해서 data를 넣어줌

        songEditor.putInt("songId", songs[nowPos].id)
        songEditor.apply()  // 내부 저장소에 값 저장

        // 반복재생 여부를 넘겨줌
        val repeatSP = getSharedPreferences("repeat", MODE_PRIVATE)
        val repeatEditor = repeatSP.edit()

        repeatEditor.putBoolean("songRepeat", repeat)
        repeatEditor.apply()
    }

    override fun onDestroy() {  // 앱이 아예 종료됐을 때
        super.onDestroy()
        timer.interrupt()  // thread 종료

        mediaPlayer?.release()  // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null  // 미디어 플레이어 해제
    }

    private fun initPlayList() {  // 플레이리스트 생성
        songs.addAll(songDB.songDao().getPlayList(true))  // 데이터베이스의 재생 목록을 가져옴
    }

    private fun initSong(){  // mainActivity에서 값 받아오기
        val songSP = getSharedPreferences("song", MODE_PRIVATE)
        val songId = songSP.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        songs[nowPos] = songDB.songDao().getSong(songs[nowPos].id)

        Log.d("now Song ID", songs[nowPos].id.toString())
    }

    private fun getPlayingSongPosition(songId : Int) : Int {
        for(i in 0 until songs.size){
            if(songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song : Song){  // 받아온 값 적용
        val music = resources.getIdentifier(song.music, "raw", this.packageName)  // MediaPlayer 생성
        mediaPlayer = MediaPlayer.create(this, music)

        binding.songMusicTitleTv.text = song.title
        binding.songSignerNameTv.text = song.singer
        binding.songAlbumIv.setImageResource(song.albumImg!!)
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 100000)/song.playTime

        if(song.isLike){  // 좋아요 버튼
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }
        else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        if(repeat) {  // 반복재생이면 반복재생이라고 표시
            binding.songRepeatOffIv.visibility = View.GONE
            binding.songRepeatOnOneIv.visibility = View.VISIBLE
        }

        mediaPlayer?.seekTo(song.current)  // seekbar 위치부터 재생

        setPlayerStatus(song.isPlaying)  // song의 Boolean 값을 따름
    }

    private fun moveSong(direct : Int) {
        if(nowPos + direct < 0) {
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos + direct >= songs.size) {
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        // 현재 상태 초기화
        resetTimer()

        // 곡 이동
        nowPos += direct
        songs[nowPos] = songDB.songDao().getSong(songs[nowPos].id)

        // 곡 재생
        startTimer()
        timerUI()
        progressUI()

        setPlayer(songs[nowPos])
        setPlayerStatus(true)
    }

    private fun repeatSong() {
        // 타이머 초기화
        resetTimer()

        // 곡 정보를 다시 불러온 후 같은 곡 다시 재생
        songs[nowPos] = songDB.songDao().getSong(songs[nowPos].id)

        startTimer()
        timerUI()
        progressUI()

        setPlayer(songs[nowPos])
        setPlayerStatus(true)
    }

    private fun initClickListener() {
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
        binding.songRepeatOffIv.setOnClickListener {  // 전체재생
            setRepeatStatus(true)
        }
        binding.songRepeatOnIv.setOnClickListener {  // 한곡재생
            repeat = true
            repeatSong()  // 한곡재생 버튼을 눌렀을 때는 thread 재시작 해주기
            setRepeatOneStatus(true)
        }
        binding.songRepeatOnOneIv.setOnClickListener {  // 기본재생으로 다시 돌아감
            repeat = false
            setRepeatOneStatus(false)
        }

        // 기본재생, 랜덤재생 관리
        binding.songRandomOffIv.setOnClickListener {
            setRandomStatus(true)
        }
        binding.songRandomOnIv.setOnClickListener {
            setRandomStatus(false)
        }

        // 좋아요 버튼 관리
        binding.songLikeIv.setOnClickListener {
            setLikeStatus(songs[nowPos].isLike)
        }

        // 다음 곡, 이전 곡 재생 관리
        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }
    }

    private fun setPlayerStatus(isPlaying : Boolean) {  // 재생, 일지정지 관리
        songs[nowPos].isPlaying = isPlaying  // 재생 값에 따라 Song의 값도 바꿔줌
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

    private fun setLikeStatus(isLike: Boolean) {  // 좋아요 관리
        songs[nowPos].isLike = !isLike  // DB 업데이트는 아님
        songDB.songDao().updateIsLikeById(!isLike , songs[nowPos].id)  // DB 업데이트

        if(songs[nowPos].isLike){  // 좋아요 버튼을 눌렀을 때
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            setToastMessage(!isLike)
        }
        else{  // 좋아요 버튼을 껐을 때
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            setToastMessage(!isLike)
        }
    }

    private fun setToastMessage(isLike: Boolean) {  // 좋아요 상황에 따른 toast message
        if(isLike){  // toast.setText가 안먹혀서.... 이렇게 나눔
            val like = layoutInflater.inflate(R.layout.toast_like, findViewById(R.id.toast_like_layout_root))

            val toast = Toast(this)

            toast.view = like
            toast.duration = Toast.LENGTH_SHORT
            toast.setGravity(Gravity.CENTER_HORIZONTAL and Gravity.FILL_HORIZONTAL, 0, 200)

            toast.show()
        }
        else{
            val unlike = layoutInflater.inflate(R.layout.toast_likeoff, findViewById(R.id.toast_unlike_layout_root))

            val toast = Toast(this)

            toast.view = unlike
            toast.duration = Toast.LENGTH_SHORT
            toast.setGravity(Gravity.CENTER_HORIZONTAL and Gravity.FILL_HORIZONTAL, 0, 200)

            toast.show()
        }
    }

    private fun startTimer(){  // 타이머 thread 시작
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    private fun resetTimer() {
        timer.interrupt()  // progress thread 정지

        mediaPlayer?.stop()  // MediaPlayer 초기화
        mediaPlayer = null

        // 현재 재생 중이던 곡 정보 초기화
        songDB.songDao().updateSecondById(0, songs[nowPos].id)
        songDB.songDao().updateIsPlayingById(false, songs[nowPos].id)
        songDB.songDao().updateCurrentById(0, songs[nowPos].id)

        // progressbar 초기화
        binding.songProgressSb.progress = 0
        setPlayerStatus(false)
    }

    private fun progressUI() {
        binding.songProgressSb.progress = ((timer.mills / songs[nowPos].playTime) * 100).toInt()  // seekbar 이동
    }

    private fun timerUI() {
        binding.songStartTimeTv.text = String.format("%02d:%02d", timer.second / 60, timer.second % 60)  // 시간도 계속 바꿔줌
    }

    inner class Timer(private val playTime : Int, var isPlaying : Boolean = true) : Thread() {  // 타이머 thread
        var second : Int = songs[nowPos].second
        var mills : Float = second.toFloat() * 1000

        override fun run() {
            super.run()
            try{
                while(true){
                    if(second >= playTime){  // 시간이 다 되면 종료
                        resetHandler.sendEmptyMessage(0)
                    }

                    if(isPlaying){  // 재생상태일 때
                        sleep(50)
                        mills += 50
                        progressHandler.sendEmptyMessage(1)

                        if(mills % 1000 == 0f){
                            timerHandler.sendEmptyMessage(2)
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