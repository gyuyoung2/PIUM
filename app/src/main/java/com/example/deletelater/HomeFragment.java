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

public class HomeFragment extends Fragment { //main 화면
    private View view; //
    private TextView textView;
    private String moisture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        textView = (TextView) view.findViewById(R.id.load_info);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.circle_moisture);

        FirebaseDatabase.getInstance().getReference().addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("MainActivity", "ChildEventListener - onChildAdded : " + dataSnapshot.getValue());

                for (DataSnapshot snapshot : dataSnapshot.child("moisture").getChildren()) {
                    Log.d("MainActivity", "ValueEventListener(add) : " + snapshot.getValue());
                    textView.setText(Integer.valueOf(snapshot.getValue().toString()).intValue()+ "%");
                    moisture = textView.getText().toString();
                    Log.d("moisture",moisture);
                    bar.setProgress(Integer.parseInt(String.valueOf(Integer.valueOf(snapshot.getValue().toString()).intValue())));
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("MainActivity", "ChildEventListener - onChildChanged : " + s);

                for (DataSnapshot snapshot : dataSnapshot.child("moisture").getChildren()) {
                    Log.d("MainActivity", "ValueEventListener(change) : " + snapshot.getValue());
                    textView.setText(Integer.valueOf(snapshot.getValue().toString()).intValue()+"%");
                    bar.setProgress(Integer.parseInt(String.valueOf(Integer.valueOf(snapshot.getValue().toString()).intValue())));
                }

            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "ChildEventListener - onChildRemoved : " + dataSnapshot.getKey());
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("MainActivity", "ChildEventListener - onChildMoved" + s);
            }
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MainActivity", "ChildEventListener - onCancelled" + databaseError.getMessage());
            }
        });

        return view;
    }
}
