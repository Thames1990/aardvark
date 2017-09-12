package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.StringJoiner;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;


/**
 * {@link BaseViewHolder ViewHolder} for {@link Measurement measurements}.
 */
public class MeasurementsViewHolder extends BaseViewHolder<Measurement> {
    
    @BindView(R.id.measurement_type)
    TextView measurementType;
    @BindView(R.id.measurement_value)
    TextView measurementValue;
    @BindView(R.id.measurement_icon)
    ImageView measurementIcon;
    @BindView(R.id.explore)
    Button explore;
    @BindView(R.id.share)
    Button share;
    
    /**
     * Creates a new ViewHolder.
     *
     * @param parent       Parent ViewGroup
     * @param itemLayoutId Layout resource id
     */
    public MeasurementsViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
        explore.setOnClickListener(this);
        share.setOnClickListener(this);
    }
    
    @Override
    protected void onBind(Measurement measurement, int position) {
        data = measurement;
        setupViews();
    }
    
    @Override
    protected void onClick(View view, Measurement measurement) {
        switch (view.getId()) {
            case R.id.explore:
                // TODO Explain measurement type
                break;
            case R.id.share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String text = data.getType().toString() + " " +
                              context.getString(R.string.measurement).toLowerCase() +
                              context.getString(R.string.with_value) + data.getValue();
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setType(context.getString(R.string.intent_type_text_plain));
                context.startActivity(Intent.createChooser(
                        shareIntent,
                        context.getResources().getText(R.string.chooser_title_share_measurement)
                ));
                break;
        }
    }
    
    
    @Override
    protected boolean onLongClick(View view, Measurement data) {
        return false;
    }
    
    /**
     * Sets up all views.
     */
    private void setupViews() {
        measurementType.setText(data.getType().toString());
        String value;
        int resId;
        switch (data.getType()) {
            case PRECIPITATION:
                value = context.getString(R.string.measurement_value_precipitation);
                resId = R.drawable.precipitation;
                break;
            case RADIATION:
                value = context.getString(R.string.measurement_value_radiation);
                resId = R.drawable.radiation;
                break;
            case TEMPERATURE:
                value = context.getString(R.string.measurement_value_temperature);
                resId = R.drawable.temperature;
                break;
            case WIND:
                value = context.getString(R.string.measurement_value_wind);
                resId = R.drawable.wind;
                break;
            default:
                value = context.getString(R.string.measurement_value_placeholder);
                resId = R.drawable.dissatisfied;
                break;
        }
        measurementValue.setText(String.format(
                Locale.getDefault(),
                value,
                data.getValue()
        ));
        measurementIcon.setImageResource(resId);
    }
}
