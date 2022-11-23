package com.example.deletelater;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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


public class SearchFragment extends Fragment {
    private View view;
    private EditText search_edit;
    private ListView listView;
    private Button button;
    XmlPullParser xpp;
    String key="20220921NVF7XYD8DH0PUMNT2RWG"; //농사로 검색 키
    Bitmap bitmap;
    PlantAdapter adapter;
    ArrayList<String> data1 = new ArrayList<>(); //검색된 식물이름 저장 변수
    ArrayList<String> imgdata = new ArrayList<>(); //검색된 식물사진 url 저장 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        search_edit = (EditText) view.findViewById(R.id.edit);
        button = view.findViewById(R.id.button);
        listView = view.findViewById(R.id.listview);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                    switch( v.getId() ){

                        case R.id.button:

                            //네트워크를 이용할 때 Thread
                            new Thread(new Runnable() {

                                @Override
                                public void run() {

                                    try {
                                        adapter = new PlantAdapter();

                                        imgdata = getXmlImage();
                                        data1 = getXmlData();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기

                                        for(int i=0;i<data1.size();i++) { //url 이미지를 호출 (검색된 갯수만큼 일대일 매칭으로 식물사진 불러옴)
                                            URL imgurl = new URL("http://www.nongsaro.go.kr/cms_contents/301/" + imgdata.get(i));
                                            HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                                            conn.setDoInput(true);
                                            conn.connect();


                                            InputStream is1 = conn.getInputStream();
                                            Log.d("img test", String.valueOf(is1));
                                            bitmap = BitmapFactory.decodeStream(is1); //bitmap으로 디코딩
                                            Log.d("test", String.valueOf(bitmap));
                                            adapter.addItem(new Plant(data1.get(i), bitmap));
                                        }

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //UI Thread(Main Thread)를 제외한 어떤 Thread도 화면을 변경할 수 없기때문에

                                    //runOnUiThread()를 이용하여 UI Thread가 TextView 글씨 변경하도록 함

                                    getActivity().runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            listView.setAdapter(adapter);
                                            search_edit.setText(null);
                                            setUpOnClickListense();
                                        }
                                    });
                                }
                            }).start();
                            break;
                    }
                }//mOnClick method..
        });
        return view;
    }


    private void setUpOnClickListense() { //해당 식물 item 클릭시..
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int currentposition = position;
                String plant = data1.get(currentposition);
                String imgsrc = imgdata.get(currentposition);
                Intent intent = new Intent(getActivity(), SearchDetailActivity.class);
                intent.putExtra("plant",plant);
                intent.putExtra("plant_img",imgsrc);
                getActivity().startActivity(intent); //searchDeatil view intent
            }
        });
    }

    //XmlPullParser를 이용하여 농사로(농촌진흥청) 에서 제공하는 OpenAPI XML 파일 파싱하기(parsing)
    ArrayList getXmlData() throws UnsupportedEncodingException { //xml 데이터 가져오기

        ArrayList<String> array = new ArrayList<>();

        String str = search_edit.getText().toString(); //EditText에 작성된 Text얻어오기

        Log.d("search name", str);

        String location = URLEncoder.encode(str, "UTF-8"); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..

        String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenList"   //요청 URL

                + "?apiKey=" + key                       //key 값

                + "&sType=sCntntsSj" //검색서비스 api명세

                + "&sText=" + location                     //검색하고자 하는 식물

                + "&wordType=cntntsSj"

                + "&numOfRows=20";                 //검색 출력 갯수

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

                    case XmlPullParser.START_DOCUMENT: //태그 진입

                        Log.d("imgpasing", "식물이름 파싱 태그 시작");

                        break;

                    case XmlPullParser.START_TAG:

                        tag = xpp.getName();    //테그 이름 얻어오기

                        if (tag.equals("item")) ;// 첫번째 검색결과

                        else if (tag.equals("cntntsSj")) {

                            xpp.next();
                            array.add(xpp.getText());
                            Log.d("array test[0]", String.valueOf(array.size())); //점검 로그
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
        return array; //StringBuffer 문자열 객체 반환


    }//getXmlData method....

    ArrayList getXmlImage() throws UnsupportedEncodingException { //식물 이미지 호출 method
        ArrayList<String> imgsrc = new ArrayList<>();
        String str = search_edit.getText().toString(); //EditText에 작성된 Text얻어오기
        String location = URLEncoder.encode(str, "UTF-8"); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..

        String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenList"   //요청 URL

                + "?apiKey=" + key                       //key 값

                + "&sType=sCntntsSj" //검색서비스 api명세

                + "&sText=" + location                     //검색한 식물명

                + "&wordType=cntntsSj"

                + "&numOfRows=100";                 //지역검색 요청값

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

                        else if (tag.equals("rtnStreFileNm")) {
                            xpp.next();
                            imgsrc.add(xpp.getText().substring(0, xpp.getText().indexOf("g") + 1)); //title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgsrc; //StringBuffer 문자열 객체 반환

    }//(Image)getXmlData method....

}
