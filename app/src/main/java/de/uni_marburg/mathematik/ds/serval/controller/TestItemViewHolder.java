package de.uni_marburg.mathematik.ds.serval.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.TestItem;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

/**
 * Created by thames1990 on 22.08.17.
 */
class TestItemViewHolder extends BaseViewHolder<TestItem> {

    private Context context;

    @BindView(R.id.overflow)
    ImageView overflow;
    @BindView(R.id.thumbnail)
    ImageView thumbnail;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.location)
    TextView tv_location;

    TestItemViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
        context = parent.getContext();
        thumbnail.setOnClickListener(this);
        overflow.setOnClickListener(this);
    }

    @Override
    protected void onBind(TestItem item, int position) {
        onBindThumbnail(item);
        onBindTime(item);
        onBindLocation(item);
    }

    private void onBindThumbnail(TestItem item) {
        Location location = item.getLocation();
        Glide
                .with(context)
                .load(String.format(
                        Locale.getDefault(),
                        context.getString(R.string.url_map_preview),
                        location.getLatitude(),
                        location.getLongitude()
                ))
                .into(thumbnail);
    }

    private void onBindTime(TestItem item) {
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - item.getTime();
        int days = (int) (diff / (24 * 60 * 60 * 1000));
        if (days == 0) {
            time.setText(context.getString(R.string.today));
        } else {
            time.setText(context.getResources().getQuantityString(R.plurals.days_ago, days, days));
        }
    }

    private void onBindLocation(TestItem item) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            } catch (SecurityException e) {
                // TODO Handle user did not grant permission
                e.printStackTrace();
            }
        }

        if (bestLocation != null) {
            tv_location.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.distance_to),
                    bestLocation.distanceTo(item.getLocation())
            ));
        }
    }

    @Override
    protected void onClick(View view, TestItem item) {
        switch (view.getId()) {
            case R.id.overflow:
                onClickOverflow();
                break;
            default:
                onClickItemview(item);
        }
    }

    private void onClickOverflow() {
        PopupMenu popup = new PopupMenu(context, overflow);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_item, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_interested:
                    Snackbar.make(
                            overflow,
                            context.getString(R.string.coming_soon),
                            Snackbar.LENGTH_SHORT
                    ).show();
                    return true;
                case R.id.action_not_interested:
                    Snackbar.make(
                            overflow,
                            context.getString(R.string.coming_soon),
                            Snackbar.LENGTH_SHORT
                    ).show();
                    return true;
            }
            return false;
        });
        popup.show();
    }

    private void onClickItemview(TestItem item) {
        Intent detail = new Intent(context, DetailActivity.class);
        detail.putExtra(DetailActivity.ITEM, item);
        context.startActivity(detail);
    }

    @Override
    protected boolean onLongClick(View view, TestItem item) {
        return false;
    }
}
