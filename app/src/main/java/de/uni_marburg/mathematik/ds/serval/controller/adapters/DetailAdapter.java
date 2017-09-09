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
 * Adapter for the detail view of an {@link Event event}.
 */
public class DetailAdapter<T extends Event> extends FragmentPagerAdapter {
    
    /**
     * Event controlled by the adapter
     */
    private T event;
    
    /**
     * Titles of the tabs
     */
    private String[] tabTitles;
    
    public DetailAdapter(Context context, FragmentManager fragmentManager, T event) {
        super(fragmentManager);
        this.event = event;
        // Needs to be updated if new tabs are added
        tabTitles = context.getResources().getStringArray(R.array.detail_tab_titles);
    }
    
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return InformationFragment.newInstance(event);
            case 1:
                return MeasurementsFragment.newInstance(event);
            default:
                return new PlaceholderFragment();
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
