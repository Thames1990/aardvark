package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Created by thames1990 on 23.08.17.
 */

public class IntroFragment extends Fragment {

    // TODO Maybe drop background color property and only set background in xml
    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private static final String PAGE = "PAGE";

    private int backgroundColor, page;

    public static IntroFragment newInstance(int backgroundColor, int page) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(BACKGROUND_COLOR, backgroundColor);
        args.putInt(PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(BACKGROUND_COLOR)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.fragment_must_contain_key_exception),
                    BACKGROUND_COLOR
            ));
        }
        backgroundColor = getArguments().getInt(BACKGROUND_COLOR);

        if (!getArguments().containsKey(PAGE)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.fragment_must_contain_key_exception),
                    PAGE
            ));
        }
        page = getArguments().getInt(PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Select a layout based on the current page
        int layoutResId;
        switch (page) {
            case 0:
                layoutResId = R.layout.fragment_intro_layout_1;
                break;
            case 1:
                layoutResId = R.layout.fragment_intro_layout_2;
                break;
            case 2:
                layoutResId = R.layout.fragment_intro_layout_3;
                break;
            case 3:
                layoutResId = R.layout.fragment_intro_layout_4;
                break;
            default:
                layoutResId = R.layout.fragment_intro_layout_1;
        }

        // Inflate the layout resource file
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(page);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        View background = view.findViewById(R.id.intro_background);
        background.setBackgroundColor(backgroundColor);
    }

}
