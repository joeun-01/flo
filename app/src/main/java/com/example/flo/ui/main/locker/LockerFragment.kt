package com.example.flo.ui.main.locker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.room.SongDatabase
import com.example.flo.databinding.FragmentLockerBinding
import com.example.flo.ui.login.LoginActivity
import com.example.flo.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    lateinit var userDB : SongDatabase

    private val information = arrayListOf("저장한 곡", "음악파일", "저장한 앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        userDB = SongDatabase.getInstance(requireActivity())!!

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp){
            tab, position ->
            tab.text= information[position]  // 각 position의 Tab에 text 연결
        }.attach()

        binding.lockerLoginTv.setOnClickListener {  // 로그인 창을 누르면 LoginActivity로 이동
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initView()  // 로그인 창으로 갔다가 돌아오면 값이 바껴야하기 때문에 onStart에서 호출
    }

    private fun getJwt() : String? {
        val sharedPreferences = requireActivity().getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)

        return sharedPreferences!!.getString("jwt", "")  // jwt 값이 없으면 0을 반환
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {  // jwt 값에 따라 로그인을 띄울지 로그아웃을 띄울지 결정
        val jwt : String? = getJwt()

        if(jwt == "") {  // jwt 값이 없을 경우
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        else {  // jwt 값이 있을 경우
            binding.lockerLoginTv.text = "로그이웃"

            binding.lockerNameTv.text = userDB.userDao().getUserName(jwt) + " 님"
            binding.lockerNameTv.visibility = View.VISIBLE

            binding.lockerLoginTv.setOnClickListener {  // 로그아웃 진행
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout() {
        val sharedPreferences = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()

        editor.remove("jwt")  // jwt에 저장된 값을 지워줌
        editor.apply()
    }
}