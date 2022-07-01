package com.example.tabexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.tabexample.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    // 권한처리는 MainActivity에서 한다.
    val permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndStart()
    }

    fun startProcess(){
        // 권한처리 후 일반 프로세스 시작.
        setContentView(R.layout.activity_main)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val viewpagerFragmentAdapter = ViewPagerAdapter(this)

        viewPager.setUserInputEnabled(false)
        viewPager.adapter = viewpagerFragmentAdapter

        //### TabLayout과 ViewPager2 연결
        // Tab 메뉴명 설정
        val tabTitles = listOf<String>("연락처", "갤러리", "프로필")
        // 연결 및 설정된 메뉴면 입히기
        TabLayoutMediator(tabLayout, viewPager, {tab, position -> tab.text = tabTitles[position]}).attach()
    }

    // 권한처리 코드
    fun checkAndStart(){
        if((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || isPermitted()){
            startProcess()
        }else{
            ActivityCompat.requestPermissions(this,permissions, 99)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isPermitted():Boolean {
        for(perm in permissions) {
            if(checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 99) {
            var check = true
            for(grant in grantResults) {
                if(grant != PackageManager.PERMISSION_GRANTED) {
                    check = false
                    break
                }
            }
            if(check) startProcess()
            else {
                Toast.makeText(this, "권한 승인 거부로 인한 앱 종료.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


}