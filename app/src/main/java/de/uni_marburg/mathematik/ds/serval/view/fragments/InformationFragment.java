package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.TestItem;

/**
 * Created by thames1990 on 24.08.17.
 */
public class InformationFragment extends Fragment {

    public static final String ITEM = "ITEM";

    private TestItem item;

    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.number_of_measurements)
    TextView number_of_measurements;
    @BindView(R.id.latitude)
    TextView latitude;
    @BindView(R.id.longitude)
    TextView longitude;
    @BindView(R.id.geohash)
    TextView geohash;

    public static InformationFragment newInstance(TestItem item) {
        InformationFragment fragment = new InformationFragment();
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
        time.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                time.getViewTreeObserver().removeOnPreDrawListener(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startPostponedEnterTransition();
                }
                return true;
            }
        });
        time.setText(new SimpleDateFormat("dd.MM.yyy", Locale.getDefault())
                .format(new Date(item.getTime())));
        number_of_measurements.setText(String.valueOf(item.getMeasurements().size()));
        latitude.setText(String.valueOf(item.getLocation().getLatitude()));
        longitude.setText(String.valueOf(item.getLocation().getLongitude()));
        geohash.setText(String.valueOf(item.getLocation().getGeohash()));
        return view;
    }
}
