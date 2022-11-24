package com.example.deletelater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; //하단 네비게이션 바
    private FragmentTransaction ft; //fragment를 위한 transaction
    private FragmentManager fm; //화면 전환을 위해 fragmentmanager

    private HomeFragment home; //홈 화면
    private SearchFragment search; //검색 화면
    private MyPlantFragment myplant; //내 식물 정보
    private ReportFragment report; //주간 리포트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){ //사용자가 터치한 itemId값을 판단해서 그 position fragment를 보여줌
                    case R.id.action_home:
                        setHome(0);
                        break;
                    case R.id.action_search:
                        setHome(1);
                        break;
                    case R.id.action_myplant:
                        setHome(2);
                        break;
                    case R.id.action_report:
                        setHome(3);
                        break;
                }
                return true;
            }
        });
        home = new HomeFragment();
        search = new SearchFragment();
        myplant = new MyPlantFragment();
        report = new ReportFragment();
        setHome(0); //첫 fragment 화면 설정해주기
    }

    private void setHome(int n){ //fragment화면 전환 switch문
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.main_frame,home);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame,search);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame,myplant);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, report);
                ft.commit();
                break;
        }

    }

}
