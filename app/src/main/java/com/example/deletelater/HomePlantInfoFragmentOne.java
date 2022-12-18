package com.example.deletelater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class HomePlantInfoFragmentOne extends Fragment { //식물_1
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    String PlantNumber = "1";
    private String UserToken = "user";
    ImageView iv_profile;
    String imgsrc;
    TextView tv_name;
    Context context;
    ViewGroup rootView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.home_moisture_1p, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.load_info); //식물 수분
        ProgressBar bar = (ProgressBar) rootView.findViewById(R.id.circle_moisture); //식물 수분 progressbar
        tv_name = (TextView) rootView.findViewById(R.id.plantName_1);
        iv_profile = (ImageView) rootView.findViewById(R.id.iv_profile_1);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        UserToken = String.valueOf(signInAccount.getId());

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getInstance().getReference(); //DatabaseReference의 인스턴스
        context = getContext();


        textView.setText("2%");
        bar.setProgress(2);


            mDatabase.child(UserToken).child("1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) { //파이어베이스 데이터를 받아오는
                    Log.d("myplant", "통과");
                    UserList user = snapshot.getValue(UserList.class); //user객체를 담는다
                    if(user.ptimage != null) {
                        Log.d("database", "통과");
                        Log.d("test", user.ptimage);
                        tv_name.setText(user.name);
                        selectFirebase(user.ptimage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { //에러 발생시
                    Log.e("Myplant", String.valueOf(error.toException()));
                }
            });



        FirebaseDatabase.getInstance().getReference().child("102634219531785912704").child("1").child("Plant1").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {// 수정본
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("firebase moisture",snapshot.getValue().toString());
                    textView.setText(snapshot.getValue().toString()+"%");
                    bar.setProgress(Integer.parseInt(String.valueOf(Integer.valueOf(snapshot.getValue().toString()).intValue())));
                }
            }
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        return rootView;
    }

    public void selectFirebase(String image) {
        ImageView PlantImage2 = (ImageView)rootView.findViewById(R.id.iv_profile_1);
        byte[] b = binaryStringToByteArray(image);
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
        PlantImage2.setImageDrawable(reviewImage);
    }



    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    // 스트링을 바이너리 바이트로
    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

}
