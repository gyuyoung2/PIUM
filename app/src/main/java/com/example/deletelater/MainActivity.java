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
    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction ft;
    private FragmentManager fm;

    private HomeFragment home;
    private SearchFragment search;
    private MyPlantFragment myplant;
    private ReportFragment report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        setHome(0);
                        break;
                    case R.id.action_search:
                        setHome(1);
                        Log.d("test","검색선택");
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

    private void setHome(int n){
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
                Log.d("test","검색됐음");
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
