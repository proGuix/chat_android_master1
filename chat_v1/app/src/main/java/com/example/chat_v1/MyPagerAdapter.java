package com.example.chat_v1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by guillaume on 12/04/16.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> listFrag;

    public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> listFrag){
        super(fm);
        this.listFrag = listFrag;
    }

    @Override
    public int getCount() {
        return this.listFrag.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public Fragment getItem(int position) {
        return this.listFrag.get(position);
    }

}
