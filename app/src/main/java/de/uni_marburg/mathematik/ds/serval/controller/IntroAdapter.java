package de.uni_marburg.mathematik.ds.serval.controller;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.uni_marburg.mathematik.ds.serval.view.fragments.IntroFragment;

/**
 * Created by thames1990 on 23.08.17.
 */
public class IntroAdapter extends FragmentPagerAdapter {

    private static final int NUMBER_OF_FRAGMENTS = 4;

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroFragment.newInstance(Color.parseColor("#f64c73"), position);
            case 1:
                return IntroFragment.newInstance(Color.parseColor("#20d2bb"), position);
            case 2:
                return IntroFragment.newInstance(Color.parseColor("#3395ff"), position);
            case 3:
                return IntroFragment.newInstance(Color.parseColor("#c873f4"), position);
            default:
                return IntroFragment.newInstance(Color.parseColor("#f64c73"), position);
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

}
