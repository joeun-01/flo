package com.example.flo
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity(), AutoLoginView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        autoLogin()

        setContentView(R.layout.activity_splash)
        //로딩화면 시작.
        splash()
    }

    private fun getJwt() : String? {
        val sharedPreferences = this.getSharedPreferences("auth2", MODE_PRIVATE)

        return sharedPreferences!!.getString("jwt", "")  // jwt 값이 없으면 0을 반환
    }

    private fun autoLogin() {  // 네트워크를 이용하여 자동로그인 진행
        val authService = AuthService()

        authService.setAutoLoginView(this)
        authService.autoLogin(getJwt())
    }

    override fun onAutoLoginSuccess(code: Int) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onAutoLoginFailure() {  // 자동로그인에 실패하면 로그인 화면으로 이동
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun splash() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            finish()
        }, 5000)
    }
}