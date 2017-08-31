package de.uni_marburg.mathematik.ds.serval.controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.view.fragments.IntroFragment;

/**
 * Adapter for the experimental intro sliders
 */
public class IntroAdapter extends FragmentPagerAdapter {

    private int[] introSlideBackgrounds;

    public IntroAdapter(FragmentManager fm, Context context) {
        super(fm);
        introSlideBackgrounds = context.getResources().getIntArray(R.array.intro_background);
    }

    @Override
    public Fragment getItem(int position) {
        return IntroFragment.newInstance(introSlideBackgrounds[position], position);
    }

    @Override
    public int getCount() {
        return introSlideBackgrounds.length;
    }

}
