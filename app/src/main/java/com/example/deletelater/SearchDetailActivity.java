package com.example.deletelater;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchDetailActivity extends AppCompatActivity  { //식물정보 detail activity


    Bitmap fBitmap;
    String fName;
    String fKey = "20220921NVF7XYD8DH0PUMNT2RWG";
    ArrayList<String> data = new ArrayList<>();
    String fNum;
    StringBuffer fMoistureData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        Intent intent = getIntent();
        fName = intent.getStringExtra("plant"); //searchfragment에서 식물 이름 데이터를 받아옴
        String imgsrc = intent.getStringExtra("plant_img"); //searchfragment에서 식물 이미지 데이터를 받아옴
        TextView textView = (TextView) findViewById(R.id.myplantDeatilName);
        ImageView imageView = (ImageView) findViewById(R.id.myplantDeatilImg);
        TextView plantNum = (TextView) findViewById(R.id.plantNum);
        ImageButton plant_register = (ImageButton) findViewById(R.id.ib_register);

        Thread mThread = new Thread(){
            public void run(){
                try {
                    data = getXmlPlantNum();
                    fNum = data.get(0);
                    fMoistureData = getXmlMoisture();
                    URL imgurl = new URL("http://www.nongsaro.go.kr/cms_contents/301/" + imgsrc);
                    HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    fBitmap = BitmapFactory.decodeStream(is);
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
            imageView.setImageBitmap(fBitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("test",imgsrc);
        textView.setText(fName);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                plantNum.setText(fMoistureData);
            }
        });

        findViewById(R.id.ib_register).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlantregisterActivity.class);
                intent.putExtra("plantDetail", (CharSequence) fMoistureData); //plant이름 detail activity로 데이터전송
                startActivity(intent);
            }
        });
    }

    ArrayList getXmlPlantNum() throws UnsupportedEncodingException { //해당 식물 일련번호 값 가져오기

        ArrayList<String> arr = new ArrayList<>();

        String str = fName; //EditText에 작성된 Text얻어오기

        String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenList"   //요청 URL

                + "?apiKey=" + fKey                       //key 값

                + "&sType=sCntntsSj"//검색서비스 api명세

                + "&sText=" + str                     //검색서비스 api명세

                + "&wordType=cntntsSj"

                + "&numOfRows=20";                 //지역검색 요청값


        try {

            URL url = new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is = url.openStream();  //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();


            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(is, "UTF-8"));  //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {


                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:

                        break;

                    case XmlPullParser.START_TAG:

                        tag = xpp.getName();    //테그 이름 얻어오기


                        if (tag.equals("item")) ;// 첫번째 검색결과


                        else if (tag.equals("cntntsNo")) {

                            xpp.next();
                            Log.d("detail test", xpp.getText());
                            arr.add(xpp.getText());
                            //줄바꿈 문자 추가

                        }

                        break;

                    case XmlPullParser.TEXT:

                        break;

                    case XmlPullParser.END_TAG:

                        tag = xpp.getName();    //테그 이름 얻어오기

                        break;
                }


                eventType = xpp.next();


            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return arr; //StringBuffer 문자열 객체 반환


    }//getXmlData method....

    StringBuffer getXmlMoisture() throws UnsupportedEncodingException { //해당 식물의 물주기 기준 값 가져오기

        StringBuffer buffer = new StringBuffer();

        String str = fNum; //식물 일련번호 가져오기
        Log.d("plant name",str);

        String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenDtl"   //요청 URL

                + "?apiKey=" + fKey                       //key 값

                + "&cntntsNo="+ str;                     //검색서비스 api명세

        Log.d("testurl", queryUrl);

        try {

            URL url = new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is = url.openStream();  //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(is, "UTF-8"));  //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("detail moisture", "루프통과");


                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:

                        break;

                    case XmlPullParser.START_TAG:
                        Log.d("detail moisture", "태그시작");

                        tag = xpp.getName();    //테그 이름 얻어오기

                        if (tag.equals("item"));// 첫번째 검색결과

                        else if (tag.equals("clCodeNm")) {

                            xpp.next();
                            Log.d("detail moisture", xpp.getText());
                            buffer.append("식물 정보 |"+xpp.getText());
                            buffer.append("\n");
                            buffer.append("\n");
                            //줄바꿈 문자 추가

                        }

                        else if (tag.equals("watercycleAutumnCodeNm")) {
                            xpp.next();
                            Log.d("detail moisture", xpp.getText());
                            buffer.append("물주기 |"+xpp.getText());
                            buffer.append("\n");
                            //줄바꿈 문자 추가
                        }

                        break;

                    case XmlPullParser.TEXT:

                        break;

                    case XmlPullParser.END_TAG:

                        tag = xpp.getName();    //테그 이름 얻어오기

                        break;
                }


                eventType = xpp.next();


            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        return buffer; //StringBuffer 문자열 객체 반환


    }//getXmlData method....

}