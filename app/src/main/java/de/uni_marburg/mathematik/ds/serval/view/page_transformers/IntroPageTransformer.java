package de.uni_marburg.mathematik.ds.serval.view.page_transformers;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.view.activities.IntroActivity;

/**
 * Defines the behaviour and animations of slides in the viewpager of the
 * {@link IntroActivity intro activity}.
 */
public class IntroPageTransformer implements ViewPager.PageTransformer {
    
    private Context context;
    
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.image)
    ImageView image;
    
    public IntroPageTransformer(Context context) {
        this.context = context;
    }
    
    @Override
    public void transformPage(View page, float position) {
        ButterKnife.bind(this, page);
        
        int pagePosition = (int) page.getTag();
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);
        
        if (position <= -1.0f || position >= 1.0f) {
            // The page is not visible
        } else if (position == 0.0f) {
            // The page is selected
        } else {
            // The page is currently being scrolled / swiped
            
            title.setAlpha(1.0f - absPosition);
            
            description.setTranslationY(-pageWidthTimesPosition / 2f);
            description.setAlpha(1.0f - absPosition);
            
            try {
                image.setContentDescription(context.getResources().getStringArray(
                        R.array.content_description_intro_image
                )[pagePosition]);
            } catch (ArrayIndexOutOfBoundsException e) {
                Crashlytics.logException(e);
            }
            image.setAlpha(1.0f - absPosition);
            image.setTranslationX(-pageWidthTimesPosition * 1.5f);
            
            if (position < 0) {
                // Swiping left
            } else {
                // Swiping right
            }
        }
    }
    
}
