package com.example.deletelater;

import static java.lang.Thread.sleep;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import androidx.fragment.app.DialogFragment;

public class PlantregisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    String data, read;
    int setid;
    ImageButton save, cancle; //save: 저장 , cancle: 뒤로가기
    EditText ptname;
    TextView pt_plant_info;
    String UserToken = "user", calanderToken;
    String PlantNumber = "1";
    String detail_data;
    Bitmap bitmap;
    ImageView user_image;
    TextView tv_plantDate;

    // 달력 UI 코드
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (year_string + "/" + month_string + "/" + day_string);
        calanderToken = dateMessage;
        tv_plantDate.setText(dateMessage);
        //날짜랑 현재 날짜 차이
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_register);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        mDatabase = FirebaseDatabase.getInstance().getReference(); //DatabaseReference의 인스턴스
        mAuth = FirebaseAuth.getInstance();

        UserToken = String.valueOf(signInAccount.getId());
        save = findViewById(R.id.save); //식물 저장
        cancle = findViewById(R.id.iv_back); //식물 등록 취소

        ptname = findViewById(R.id.et_plantname); //식물 이름 작성하기
        pt_plant_info = findViewById(R.id.tv_plantDetail); //식물 detail 가져오기
        user_image = findViewById(R.id.user_image);

        Intent intent = getIntent();
        detail_data = intent.getStringExtra("plantDetail"); //searchfragment에서 식물 이름 데이터를 받아옴
        String imgsrc = intent.getStringExtra("plant_img"); //searchfragment에서 식물 이미지 데이터를 받아옴
        pt_plant_info.setText(detail_data);

        tv_plantDate = findViewById(R.id.tv_plantDate);

        readCount();



        Button picBtn = findViewById(R.id.btn_UploadPicture);
        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultPicture.launch(intent);
            }
        });



        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchFragment.class);
                startActivity(intent);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String simage = updateImage(); //이미지 비트맵으로 변환

                String getPlantName = ptname.getText().toString();
                String getPlantDetail = pt_plant_info.getText().toString();
                String getPlantNumber = PlantNumber;
                String getPlantImage = simage;

                HashMap result = new HashMap<>();
                result.clear();
                result.put("name", getPlantName); //키, 값
                result.put("date", calanderToken);
                result.put("pttype", getPlantDetail);
                result.put("ptseason", getPlantDetail);
                result.put("PlantNumber", getPlantNumber);
                result.put("PlantImage", getPlantImage);

                writeUser(getPlantName, calanderToken, getPlantDetail, getPlantNumber, getPlantImage); //등록

            }
        });

//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DeletePlantData();
//            }
//        });  삭제할때 쓰기

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    private void writeUser(String name, String date, String plantDetail, String PtNum,String Ptimage) {
        User user = new User(name, date, plantDetail ,Ptimage);
        String Ptnumber = String.valueOf(PtNum);

        mDatabase.child(UserToken).child(Ptnumber).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() { //데이터베이스에 넘어간 이후 처리
                    @Override
                    public void onSuccess(Void aVoid) {
                        //readCount();
                        Toast.makeText(getApplicationContext(), "저장을 완료했습니다", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PlantregisterActivity.this, HomeFragment.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "저장에 실패했습니다", Toast.LENGTH_LONG).show();
                    }
                });

        WritePlantNumber();
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


    /*
    // 데이터베이스에서 정보 불러오기,
    userId = UserToken(유저고유번호) 하위문서에 있는 식물 번호를 뜻합니다.
     */



    private void WritePlantNumber() {
        int plantnumber = Integer.parseInt(PlantNumber);
        plantnumber = plantnumber + 1;
        PlantNumber = String.valueOf(plantnumber);
        mDatabase.child(UserToken).child("PlantNumber").setValue(PlantNumber);
    }


//    private void DeletePlantData() {
//        String getDeleteNumber = deletenumber.getText().toString();
//        mDatabase.child(UserToken).child("PlantNumber").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//
//                } else { //PlantCount의 값 가져오기
//
//                    String number = String.valueOf(task.getResult().getValue());
//                    int var = Integer.parseInt(number);
//                    int var2 = Integer.parseInt(getDeleteNumber);
//
//                    //var = Plant최대값
//                    //var2 = 삭제하고 싶은
//                    Log.d("MyTag","var1="+var);
//                    Log.d("MyTag","var2="+var2);
//
//                    int i;
//                    for(i=var2;i<var-1;i++) {
//                        ResetPlant(i, i + 1);
//                    }
//
//                    Log.d("MyTag","DeleteComplete");
//                    var --;
//                    number = String.valueOf(var);
//                    mDatabase.child(UserToken).child(number).removeValue();
//                    mDatabase.child(UserToken).child("PlantNumber").setValue(number);
//
//
//                    Toast.makeText(getApplicationContext(), "변경을 완료했습니다", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });
//    }

//    private void ResetPlant(int num1, int num2) {
//
//        String swap1 = String.valueOf(num1);
//        String swap2 = String.valueOf(num2);
//        Log.d("MyTag","swap1="+swap1);
//        Log.d("MyTag","swap2="+swap2);
//
//        mDatabase.child(UserToken).child(swap2).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("name").setValue(val);
//                }
//            }
//
//        });
//
//        mDatabase.child(UserToken).child(swap2).child("ptgrowth").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("ptgrowth").setValue(val);
//                }
//            }
//
//        });
//
//        mDatabase.child(UserToken).child(swap2).child("ptlight").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("ptlight").setValue(val);
//                }
//            }
//
//        });
//
//        mDatabase.child(UserToken).child(swap2).child("ptmoisture").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("ptmoisture").setValue(val);
//                }
//            }
//
//        });
//
//        mDatabase.child(UserToken).child(swap2).child("ptseason").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("ptseason").setValue(val);
//                }
//            }
//
//        });
//
//        mDatabase.child(UserToken).child(swap2).child("pttype").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("pttype").setValue(val);
//                }
//            }
//
//        });
//
//        mDatabase.child(UserToken).child(swap2).child("ptimage").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) { //실패
//                } else {
//                    String val;
//                    val = String.valueOf(task.getResult().getValue());
//                    mDatabase.child(UserToken).child(swap1).child("ptimage").setValue(val);
//                }
//            }
//
//        });
//
//    }


    public String updateImage() {
        ImageView PlantImage = (ImageView)findViewById(R.id.user_image);
        Drawable image = PlantImage.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] reviewImage = stream.toByteArray();
        String simage = byteArrayToBinaryString(reviewImage);
        return simage;
    }

    // 바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    //결과 OK , 데이터 null 아니면
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        Bundle extras = result.getData().getExtras();

                        bitmap = (Bitmap) extras.get("data");

                        user_image.setImageBitmap(bitmap);
                    }
                }
            });
}


