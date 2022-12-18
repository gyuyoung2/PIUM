package com.example.deletelater;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPlantFragment extends Fragment  {
    private View view;
    String PlantNumber = "1";
    private String UserToken = "user";
    private ListView listView; //식물검색 결과 리스트
    TextView textView;
    String plantName;
    String plantImage;
    String data;
    String imgsrc;


    private UserAdapter adapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myplant, container, false);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        UserToken = String.valueOf(signInAccount.getId());
        textView = view.findViewById(R.id.test);

        listView = (ListView) view.findViewById(R.id.myplant_list);
        adapter = new UserAdapter();

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getInstance().getReference(); //DatabaseReference의 인스턴스

        mDatabase.child(UserToken).child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //파이어베이스 데이터를 받아오는
                Log.d("myplant", "통과");
                UserList user = snapshot.getValue(UserList.class); //user객체를 담는다
                Log.d("database", "통과");
                data = user.name; //담은 데이터를 배열리스트에 추가
                imgsrc = user.ptimage;
                Log.d("test", data);
                arrayList.add(data);
                arrayList.add(imgsrc);
                Log.d("test1", arrayList.get(0));
                Log.d("test1", arrayList.get(1));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //에러 발생시
                Log.e("Myplant", String.valueOf(error.toException()));
            }
        });
        listView.setAdapter(adapter);


        return view;
    }
    private void readUser(String PlantNumber) {
        //데이터 읽기
        int Count = Integer.valueOf(PlantNumber);
            String num = Integer.toString(Count);

            mDatabase.child(UserToken).child("1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserList user = snapshot.getValue(UserList.class);
                    plantName = user.name;
                    plantImage = user.ptimage;

//                    listView.setAdapter(adapter);
//                    adapter.addItem(plantName,plantImage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                    Toast.makeText(getContext().getApplicationContext(), "데이터를 가져오는데 실패했습니다", Toast.LENGTH_LONG).show();
                }
            });
        }
    private void readCount() {
        mDatabase.child(UserToken).child("PlantNumber").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) { //실패
                } else { //PlantCount의 값 가져오기
                    PlantNumber = String.valueOf(task.getResult().getValue());
                    if (PlantNumber == "null") { //한번도 정보를 넣은적이 없을때 (계정 새로 생성직후)
                        PlantNumber = "1";
                    }
                    else{

                    }
                }
            }

        });
    }
    }


