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
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

/**
 * ViewHolder for {@link GenericEvent generic events}
 */
class GenericEventViewHolder extends BaseViewHolder<GenericEvent> {

    private Context context;

    @BindView(R.id.overflow)
    ImageView overflow;
    @BindView(R.id.thumbnail)
    ImageView thumbnail;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.location)
    TextView tv_location;

    GenericEventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
        context = parent.getContext();
        thumbnail.setOnClickListener(this);
        overflow.setOnClickListener(this);
    }

    @Override
    protected void onBind(GenericEvent event, int position) {
        setupThumbnail(event);
        setupTime(event);
        setupLocation(event);
    }

    /**
     * Loads a static image of the location of the event.
     *
     * @param event The event to load the static image of the position for
     */
    private void setupThumbnail(GenericEvent event) {
        Location location = event.getLocation();
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

    /**
     * Sets the elapsed days since an {@link Event event} happened.
     *
     * @param event
     */
    private void setupTime(GenericEvent event) {
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - event.getTime();
        int days = (int) (diff / (24 * 60 * 60 * 1000));
        if (days == 0) {
            time.setText(context.getString(R.string.today));
        } else {
            time.setText(context.getResources().getQuantityString(R.plurals.days_ago, days, days));
        }
    }

    private void setupLocation(GenericEvent item) {
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
    protected void onClick(View view, GenericEvent event) {
        switch (view.getId()) {
            case R.id.overflow:
                onClickOverflow();
                break;
            default:
                onClickItemview(event);
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

    private void onClickItemview(GenericEvent item) {
        Intent detail = new Intent(context, DetailActivity.class);
        detail.putExtra(DetailActivity.ITEM, item);
        context.startActivity(detail);
    }

    @Override
    protected boolean onLongClick(View view, GenericEvent event) {
        return false;
    }
}
