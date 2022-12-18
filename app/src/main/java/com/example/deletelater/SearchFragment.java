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


public class SearchFragment extends Fragment { //식물검색 fragment..
    private EditText search_edit; //사용자 검색 창
    private ListView listView; //식물검색 결과 리스트
    XmlPullParser xpp; //pasing 받는 변수
    String fKey ="20220921NVF7XYD8DH0PUMNT2RWG"; //농사로 인증키
    Bitmap fBitmap; //url이미지가 인코딩이된 비트맵 식물사진
    PlantAdapter adapter; //listview에 전달하기 위한 어뎁터
    ArrayList<String> plantNameData = new ArrayList<>(); //검색된 식물이름 저장 변수
    ArrayList<String> imgdata = new ArrayList<>(); //검색된 식물사진 url 저장 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        search_edit = (EditText) view.findViewById(R.id.edit);
        Button button = view.findViewById(R.id.button);
        listView = view.findViewById(R.id.listview);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d("onClick","사용자 터치");
                    switch( v.getId() ){
                        case R.id.button:
                            //네트워크를 이용할 때 Thread
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d("run","Thread 진입");
                                        adapter = new PlantAdapter();

                                        imgdata = getXmlImage();
                                        plantNameData = getXmlData();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
                                        for(int i = 0; i< plantNameData.size(); i++) { //url 이미지를 호출 (검색된 갯수만큼 일대일 매칭으로 식물사진 불러옴)
                                            Log.d("for","loop 진입");
                                            URL imgurl = new URL("http://www.nongsaro.go.kr/cms_contents/301/" + imgdata.get(i));
                                            HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                                            conn.setDoInput(true);
                                            conn.connect();
                                            InputStream is1 = conn.getInputStream();
                                            Log.d("img test", String.valueOf(is1));
                                            fBitmap = BitmapFactory.decodeStream(is1); //bitmap으로 디코딩
                                            Log.d("test", String.valueOf(fBitmap));
                                            adapter.addItem(new Plant(plantNameData.get(i), fBitmap));
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

    private void setUpOnClickListense() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override //해당 식물 item 클릭시..
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int currentposition = position; //사용자가 터치한 list index
                String plant = plantNameData.get(currentposition);
                String imgsrc = imgdata.get(currentposition);
                Intent intent = new Intent(getActivity(), SearchDetailActivity.class); //Mainactivty -> SearchDeatilActivity
                intent.putExtra("plant",plant); //plant이름 detail activity로 데이터전송
                intent.putExtra("plant_img",imgsrc); //plant이미지 url source activity로 데이터전송
                getActivity().startActivity(intent); //searchDeatil view intent
            }
        });
    }

    //XmlPullParser를 이용하여 농사로(농촌진흥청) 에서 제공하는 OpenAPI XML 파일 파싱하기(parsing)
    ArrayList getXmlData() throws UnsupportedEncodingException { //xml 데이터 가져오기
        Log.d("IngetXmlData","method 진입");

        ArrayList<String> array = new ArrayList<>(); //태그 안 데이터 임시저장

        String str = search_edit.getText().toString(); //EditText에 작성된 Text얻어오기

        Log.d("search name", str); //사용자가 입력한 text 점검로그

        String location = URLEncoder.encode(str, "UTF-8"); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..

        String queryUrl = "http://api.nongsaro.go.kr/service/garden/gardenList"   //요청 URL

                + "?apiKey=" + fKey                       //api 인증 key 값

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

            while (eventType != XmlPullParser.END_DOCUMENT) { //태그가 끝날 때까지 반복

                switch (eventType) {

//                    case XmlPullParser.START_DOCUMENT: //태그 진입
//                        Log.d("imgpasing", "식물이름 파싱 태그 시작"); //test log
//                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();    //테그 이름 얻어오기

//                        if (tag.equals("item")) ;// 첫번째 검색결과

                        if (tag.equals("cntntsSj")) {
                            xpp.next();
                            array.add(xpp.getText());//파싱을 한 문자열을 arraylist에 저장
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

                + "?apiKey=" + fKey                       //key 값

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

            while (eventType != XmlPullParser.END_DOCUMENT) { //태그가 끝날 때까지 반복

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT: //태그진입
                        break;

                    case XmlPullParser.START_TAG:

                        tag = xpp.getName();    //테그 이름 얻어오기

                        if (tag.equals("item")) ;// 첫번째 검색결과

                        else if (tag.equals("rtnStreFileNm")) {
                            xpp.next();
                            imgsrc.add(xpp.getText().substring(0, xpp.getText().indexOf("g") + 1)); //rtnStreFileNm요소의 TEXT 읽어와서 문자열ArrayList에 추가
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

}//SearchFragment class...
