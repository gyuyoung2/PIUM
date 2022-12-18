package com.example.deletelater;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment { //home

    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 mIndicator;
    private TextView textView;
    private TextView tv_username; //닉네임 text
    private ImageView iv_profile; //프로필 사진
    private DatabaseReference mDatabase;
    TextView tv_humidity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false); //home fragment view 추가
        //ViewPager2
        mPager = view.findViewById(R.id.viewpager);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Adapter
        pagerAdapter = new ViewPaserAdapter(getActivity(), num_page);
        mPager.setAdapter(pagerAdapter);
        //Indicator
        mIndicator = view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page,0);
        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(3);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.circle_humidity);
        tv_humidity = (TextView) view.findViewById(R.id.tv_humidity);
        bar.setProgress(2555412);
        Intent intent = getActivity().getIntent();
        String nickname = intent.getStringExtra("nickname");
        String photoURL = intent.getStringExtra("profileUrl");
        tv_username = view.findViewById(R.id.tv_username);
        tv_username.setText(nickname); //닉네임 text를 텍스트뷰에 세팅
        iv_profile = view.findViewById(R.id.btn_user);
        Glide.with(getActivity()).load(photoURL).into(iv_profile); //프로필 url을 이미지 버튼에 세팅
        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position%num_page);
            }

        });

        final float pageMargin= getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);
        mPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float myOffset = position * -(2 * pageOffset + pageMargin);
                if (mPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(mPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.setTranslationX(-myOffset);
                    } else {
                        page.setTranslationX(myOffset);
                    }
                } else {
                    page.setTranslationY(myOffset);
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("102634219531785912704").child("1").child("Humidity").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {// 수정본
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("firebase moisture",snapshot.getValue().toString());
                    tv_humidity.setText(snapshot.getValue().toString()+"%");
                    bar.setProgress(Integer.parseInt(String.valueOf(Integer.valueOf(snapshot.getValue().toString()).intValue())));
                }
            }
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }
}
