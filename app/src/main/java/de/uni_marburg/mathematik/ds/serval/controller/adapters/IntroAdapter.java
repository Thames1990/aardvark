package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.view.fragments.IntroFragment;

/**
 * Adapter for intro slides in the
 * {@link de.uni_marburg.mathematik.ds.serval.view.activities.IntroActivity intro activity}.
 */
public class IntroAdapter extends FragmentPagerAdapter {
    
    /**
     * Colors for the intro slides
     * <p>
     * Is defined by {@link de.uni_marburg.mathematik.ds.serval.R.array#intro_backgrounds a
     * resource}.
     */
    private int[] introSlideBackgroundColors;
    
    public IntroAdapter(FragmentManager fm, Context context) {
        super(fm);
        introSlideBackgroundColors = context.getResources().getIntArray(R.array.intro_backgrounds);
    }
    
    @Override
    public Fragment getItem(int position) {
        return IntroFragment.newInstance(introSlideBackgroundColors[position], position);
    }
    
    @Override
    public int getCount() {
        return introSlideBackgroundColors.length;
    }
    
}
