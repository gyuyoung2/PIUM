package com.example.deletelater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction ft;
    private FragmentManager fm;

    private home home;
    private search search;
    private myplant myplant;
    private report report;

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
        home = new home();
        search = new search();
        myplant = new myplant();
        report = new report();
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
