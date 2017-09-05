package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.view.fragments.InformationFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.MeasurementsFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.PlaceholderFragment;

/**
 * Adapter for the detail view of an {@link Event event}
 */
public class DetailAdapter<T extends Event> extends FragmentPagerAdapter {
    
    private T event;
    private String[] tabTitles;
    
    public DetailAdapter(FragmentManager fm, T event, Context context) {
        super(fm);
        this.event = event;
        // Needs to be updated if new tabs are added
        tabTitles = context.getResources().getStringArray(R.array.detail_tabs);
    }
    
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return InformationFragment.newInstance(event);
            case 1:
                return MeasurementsFragment.newInstance(event);
            default:
                return PlaceholderFragment.newInstance();
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
