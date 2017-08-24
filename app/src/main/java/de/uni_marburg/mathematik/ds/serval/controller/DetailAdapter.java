package de.uni_marburg.mathematik.ds.serval.controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.TestItem;
import de.uni_marburg.mathematik.ds.serval.view.fragments.InformationFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.MeasurementsFragment;

/**
 * Created by thames1990 on 23.08.17.
 */
public class DetailAdapter extends FragmentPagerAdapter {

    private TestItem item;
    private String[] tabTitles;

    public DetailAdapter(FragmentManager fm, TestItem item, Context context) {
        super(fm);
        this.item = item;
        tabTitles = context.getResources().getStringArray(R.array.detail_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return InformationFragment.newInstance(item);
            case 1:
                return MeasurementsFragment.newInstance(item);
            default:
                return InformationFragment.newInstance(item);
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
