package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.TestItem;

/**
 * Created by thames1990 on 24.08.17.
 */

public class MeasurementsFragment extends Fragment {

    public static final String ITEM = "ITEM";

    private TestItem item;

    public static MeasurementsFragment newInstance(TestItem item) {
        MeasurementsFragment fragment = new MeasurementsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(ITEM)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.fragment_must_contain_key_exception),
                    ITEM
            ));
        }
        item = (TestItem) getArguments().getSerializable(ITEM);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
