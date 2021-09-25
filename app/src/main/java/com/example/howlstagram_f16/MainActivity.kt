package com.example.howlstagram_f16

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.howlstagram_f16.navigation.AlarmFragment
import com.example.howlstagram_f16.navigation.DetailViewFragment
import com.example.howlstagram_f16.navigation.GridFragment
import com.example.howlstagram_f16.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
//        when을 이용해서 item을 눌렀을 때 화면이 넘어갈 수 있도록 설정해준다.

//        - supportFragmenetManager.beginTransaction() 은 Fragment 추가, 제거, 변경 등을 할 수 있다.
//        - replace 는 그 중에 변경을 뜻함 ( add - 추가 / remove - 제거 ) - 2개의 인자를 가짐
//        - 첫 번째 인자는 기존의 fragment가 있는 곳, 두 번째 인자는 바꿀 fragment
//        - commit()을 작성해서 변경내용 저장됨

        when(p0.itemId){
            R.id.action_home->{
                var detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                return true
            }
            R.id.action_search->{
                var gridFragment = GridFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                return true
            }
            R.id.action_add_photo->{

                return true
            }
            R.id.action_favorite_alarm->{
                var alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                return true
            }
            R.id.action_account->{
                var userFragment = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }
}