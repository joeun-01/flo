package com.example.flo.ui.main

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.example.flo.R
import com.example.flo.room.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivityMainBinding
import com.example.flo.ui.song.SongActivity
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.ui.main.search.SearchFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var mediaPlayer : MediaPlayer? = null
    lateinit var progress : Progress

    private val songs = arrayListOf<Song>()
    lateinit var songDB : SongDatabase  // 데이터베이스의 song 목록을 가져와서 songs에 저장
    var nowPos = 0

    var progressHandler = Handler(Looper.getMainLooper()) {  // progress bar를 업데이트하기 위한 핸들러
        progressBar()
        true
    }

    var resetHandler = Handler(Looper.getMainLooper()){  // 재생이 끝나면 다시 처음으로 되돌아가기 위한 핸들러
        resetProgress()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {  // 최초 실행 시 해야할 작업들
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songDB = SongDatabase.getInstance(this)!!

        inputDummySongs()
        inputDummyAlbums()

        initPlayList()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)

        if(songId == 0){
            songs[nowPos] = songDB.songDao().getSong(1)
        }


        binding.mainPlayerCl.setOnClickListener {  // SongActivity로 전환
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener {  // 정지
            setPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {  // 재생
            setPlayerStatus(false)
        }

        // 다음 곡, 이전 곡 재생 관리
        binding.mainMiniplayerAfter.setOnClickListener {
            moveSong(+1)
        }
        binding.mainMiniplayerBefore.setOnClickListener {
            moveSong(-1)
        }

        binding.mainProgressSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar : SeekBar?) {  // 터치 시작

            }

            override fun onProgressChanged(seekBar : SeekBar?, progress : Int, fromUser : Boolean) {  // 터치 중

            }

            override fun onStopTrackingTouch(seekBar : SeekBar?) {  // 터치 끝
                progress.interrupt()

                // 변경된 값을 받아옴
                binding.mainProgressSb.progress = seekBar!!.progress
                songs[nowPos].second = (seekBar.progress * songs[nowPos].playTime) / 100000
                mediaPlayer?.seekTo(binding.mainProgressSb.progress)

                // 변경된 값을 UI에 적용
                startProgress()

                Log.d("progress 변경 완료", seekBar.progress.toString())
            }
        })

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())

        initBottomNavigation()

    }

    override fun onStart() {  // 다시 MainActivity로 돌아왔을 때 할 작업
        super.onStart()

        initSong()

        startProgress()
        progressBar()
        setMiniPlayer(songs[nowPos])

        Log.d("현재 song 값", songs[nowPos].toString())
    }

    override fun onPause() {
        super.onPause()
        progress.interrupt()

        songDB.songDao().updateSecondById(((binding.mainProgressSb.progress * songs[nowPos].playTime)/100)/1000, songs[nowPos].id)
        songDB.songDao().updateIsPlayingById(songs[nowPos].isPlaying, songs[nowPos].id)

        mediaPlayer?.stop()
        songDB.songDao().updateCurrentById(mediaPlayer!!.currentPosition, songs[nowPos].id)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌

        editor.putInt("songId", songs[nowPos].id)
        editor.apply()  // 내부 저장소에 값 저장
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()  // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null  // 미디어 플레이어 해제

        progress.interrupt()
        songs[nowPos].isPlaying = false

        // MainActivity, SongActivity의 데이터를 저장하고 종료
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()  // 에디터를 통해서 data를 넣어줌

        editor.putInt("songId", songs[nowPos].id)

        editor.apply()  // 내부 저장소에 값 저장
    }

    private fun getJwt() : String? {
        val sharedPreferences = this.getSharedPreferences("auth2", MODE_PRIVATE)

        return sharedPreferences!!.getString("jwt", "")  // jwt 값이 없으면 0을 반환
    }

    private fun initPlayList() {  // 플레이리스트 생성
        songs.addAll(songDB.songDao().getPlayList(true))
    }

    private fun initSong(){
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)

        songs[nowPos] = songDB.songDao().getSong(songs[nowPos].id)
    }

    private fun getPlayingSongPosition(songId : Int) : Int {
        for(i in 0 until songs.size){
            if(songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayerStatus(isPlaying : Boolean){  // 재생 여부 관리
        songs[nowPos].isPlaying = isPlaying  // play 여부 동기화
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

    private fun moveSong(direct : Int) {
        // 곡 이동 제한
        if(nowPos + direct < 0) {  // 플레이리스트 첫 곡일 때
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos + direct >= songs.size) {  // 플레이리스트 마지막 곡일 때
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        // progress reset
        resetProgress()

        // 곡 이동
        nowPos += direct
        songs[nowPos] = songDB.songDao().getSong(songs[nowPos].id)  // 새로운 data 받아오기

        // 곡 재생
        startProgress()
        progressBar()

        setMiniPlayer(songs[nowPos])
        setPlayerStatus(true)
    }

    fun changeSong() {  // 앨범의 수록곡 재생
        // progressbar reset
        resetProgress()

        // 해당 수록곡을 플레이리스트에 추가해줌
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)  // 새로운 노래 정보 다운로드
        val songID = sharedPreferences.getInt("songId", 0)

        nowPos++  // 추가 위치는 현재 재생 곡 뒤에
        songs.add(nowPos, songDB.songDao().getSong(songID))
        songDB.songDao().updateIsInPlaylistById(true, songID)

        songs[nowPos] = songDB.songDao().getSong(songID)

        // 음악 초기화
        startProgress()
        progressBar()

        setMiniPlayer(songs[nowPos])
        setPlayerStatus(true)
    }

    fun playAlbum(albumIdx : Int) {  // 앨범 전체 재생
        // progressbar reset
        resetProgress()

        // 플레이리스트 초기화
        updatePlaylist(false)  // 플레이리스트에서 제거
        songs.clear()

        // 플레이리스트를 해당 앨범으로 바꿈
        songs.addAll(songDB.songDao().getSongsInAlbum(albumIdx))
        updatePlaylist(true)  // 플레이리스트로 삽입

        // 앨범의 첫 곡부터 재생
        nowPos = 0
        startProgress()
        progressBar()

        setMiniPlayer(songs[0])
        setPlayerStatus(true)
    }

    private fun updatePlaylist(change : Boolean) {
        for(i in 0 until songs.size) {  // songs 안에 있는 곡들의 isInPlaylist 값을 모두 바꿔줌
            songDB.songDao().updateIsInPlaylistById(change, songs[i].id)
        }
    }

    private fun startProgress(){  // Progress thread 시작
        progress = Progress(songs[nowPos].playTime, songs[nowPos].isPlaying)
        progress.start()
    }

    private fun progressBar(){  // UI는 main Thread에서 변경
        binding.mainProgressSb.progress = ((progress.mills / songs[nowPos].playTime) * 100).toInt()
    }

    private fun resetProgress() {
        progress.interrupt()  // progress thread 정지

        mediaPlayer?.stop()  // MediaPlayer 초기화
        mediaPlayer = null

        // 현재 재생 중이던 곡 정보 초기화
        songDB.songDao().updateSecondById(0, songs[nowPos].id)
        songDB.songDao().updateIsPlayingById(false, songs[nowPos].id)
        songDB.songDao().updateCurrentById(0, songs[nowPos].id)

        // progressbar 초기화
        binding.mainProgressSb.progress = 0
        setPlayerStatus(false)
    }

    inner class Progress(private val playTime : Int, var isPlaying : Boolean) : Thread() {  // progressBar 변경 thread
        var second : Int = songs[nowPos].second
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
                false,
                0,
                true
            )
        )

        songDB.songDao().insert(
            Song(
                "02",
                "말리지 마",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false,
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "03",
                "VILLAIN DIES",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false,
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "04",
                "ALREADY",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false,
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "05",
                "POLAROID",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false,
                0,
                false
            )
        )
        songDB.songDao().insert(
            Song(
                "06",
                "ESCAPE",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false,
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "07",
                "LIAR",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                180,
                false,
                "music_tomboy",
                0,
                false,
                0,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "08",
                "MY BAG",
                "(여자)아이들",
                R.drawable.img_album_exp13,
                0,
                160,
                false,
                "music_mybag",
                0,
                false,
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
                false,
                1,
                true
            )
        )

        songDB.songDao().insert(
            Song(
                "02",
                "Flu",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "03",
                "Coin",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "04",
                "봄 안녕 봄",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "05",
                "Celebrity",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "06",
                "돌림노래 (Feat. DEAN)",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "07",
                "빈 컵 (Empty Cup)",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "08",
                "아이와 나의 바다",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "09",
                "어푸 (Ah puh)",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
                false
            )
        )

        songDB.songDao().insert(
            Song(
                "10",
                "에필로그",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                0,
                215,
                false,
                "music_lilac",
                0,
                false,
                1,
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
                "music_nextlevel",
                0,
                false,
                2,
                true
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
                "music_boywithluv",
                0,
                false,
                3,
                true
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
                "music_bboombboom",
                0,
                false,
                4,
                true
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
                "music_weekend",
                0,
                false,
                5,
                true
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun inputDummyAlbums(){
        val albums = songDB.albumDao().getAlbums()

        if(albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                0,"TOMBOY", "(여자)아이들", R.drawable.img_album_exp13
            )
        )

        songDB.albumDao().insert(
            Album(
                1,"LILAC", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                2,"Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                3,"Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                4,"BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5
            )
        )

        songDB.albumDao().insert(
            Album(
                5,"Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6
            )
        )
    }

}