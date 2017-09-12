package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.Serval;

/**
 * Created by thames1990 on 09.09.17.
 */
public abstract class BaseFragment extends Fragment {
    
    private Unbinder unbinder;
    
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(getLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Serval.getRefWatcher(getContext()).watch(this);
    }
    
    /**
     * Is used to get the layout for {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup,
     * Bundle)}
     *
     * @return ID for an XML layout resource to load (e.g., R.layout.main_page)
     */
    protected abstract int getLayout();
}
