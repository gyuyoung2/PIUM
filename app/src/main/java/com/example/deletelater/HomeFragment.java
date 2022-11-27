package com.example.deletelater;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment { //home

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false); //home fragment view 추가
        TextView textView = (TextView) view.findViewById(R.id.load_info); //식물 이름
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.circle_moisture); //식물 수분 progressbar

        FirebaseDatabase.getInstance().getReference().child("Users").child("User2").child("Plant1").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {// 식물의 데이터 값 가져오기
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("firebase moisture",snapshot.getValue().toString());
                    textView.setText(snapshot.getValue().toString()+"%");
                    bar.setProgress(Integer.parseInt(String.valueOf(Integer.valueOf(snapshot.getValue().toString()).intValue())));
                }
            }
            public void onCancelled(DatabaseError databaseError) { //오류 처리
            }
        });

        return view;
    }
}
