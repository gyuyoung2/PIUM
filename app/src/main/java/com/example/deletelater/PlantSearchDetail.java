package com.example.deletelater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlantSearchDetail extends AppCompatActivity  {


    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_search_detail);
        Intent intent = getIntent();
        String name = intent.getStringExtra("plant");
        String imgsrc = intent.getStringExtra("plant_img");
        TextView textView = (TextView) findViewById(R.id.myplantDeatilName);
        ImageView imageView = (ImageView) findViewById(R.id.myplantDeatilImg);
        Thread mThread = new Thread(){
            public void run(){
                try {
                    URL imgurl = new URL("http://www.nongsaro.go.kr/cms_contents/301/" + imgsrc);
                    HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try{
            mThread.join();
            imageView.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("test",imgsrc);
        textView.setText(name);
    }

}