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
 * Created by thames1990 on 09.09.17.
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
                break;
            case R.id.share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                if (VERSION.SDK_INT >= VERSION_CODES.N) {
                    StringJoiner joiner = new StringJoiner(" ");
                    joiner.add(data.getType().toString())
                          .add(context.getString(R.string.measurement))
                          .add("with value")
                          .add(String.valueOf(data.getValue()));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, joiner.toString());
                } else {
                    String text = data.getType().toString() +
                                  " " +
                                  context.getString(R.string.measurement).toLowerCase() +
                                  " with value " +
                                  data.getValue();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                }
                shareIntent.setType(context.getString(R.string.intent_type_text_plain));
                context.startActivity(Intent.createChooser(
                        shareIntent,
                        context.getResources()
                               .getText(R.string.chooser_title_share_measurement)
                ));
                break;
        }
    }
    
    
    @Override
    protected boolean onLongClick(View view, Measurement data) {
        return false;
    }
    
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
