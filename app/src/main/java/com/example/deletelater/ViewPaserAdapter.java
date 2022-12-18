package com.example.deletelater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class ViewPaserAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    public int mCount;

    public ViewPaserAdapter(@NonNull FragmentActivity fa, int count)
    {
        super(fa);
        mCount = count;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index==0) return new HomePlantInfoFragmentOne();
        else if(index==1) return new HomePlantInfoFragmentSecond();
        else return new HomePlantInfoFragmentThird();
    }

    @Override
    public int getItemCount() {
        return 2000;
    }

    public int getRealPosition(int position) { return position % mCount; }

}
