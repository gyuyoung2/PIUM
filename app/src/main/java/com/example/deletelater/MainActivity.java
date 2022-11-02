package com.example.deletelater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView textView;
//    TextView textView2;
    String test = "93";
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.load_info);
        ProgressBar bar = (ProgressBar) findViewById(R.id.circle_moisture);

//        editText = findViewById(R.id.edit_id);
//        button = (Button) findViewById(R.id.read_btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.d("MainActivity", "Button - onClickListener");
//                FirebaseDatabase.getInstance().getReference().push().setValue(editText.getText().toString());
//            }
//        });

        FirebaseDatabase.getInstance().getReference().addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("MainActivity", "ChildEventListener - onChildAdded : " + dataSnapshot.getValue());

                for (DataSnapshot snapshot : dataSnapshot.child("moisture").getChildren()) {
                        Log.d("MainActivity", "ValueEventListener(add) : " + snapshot.getValue());
                        textView.setText(Integer.valueOf(snapshot.getValue().toString()).intValue()+ "%");
                        test = textView.getText().toString();
                        Log.d("test",test);
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

//        bar.setProgress(Integer.valueOf(textView.getText().toString()));
//        bar.setProgress(Integer.parseInt(textView.getText().toString()));

    }

}
