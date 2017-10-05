package de.uni_marburg.mathematik.ds.serval.view.activities

import android.Manifest
import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import de.uni_marburg.mathematik.ds.serval.R


class IntroActivity : IntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonBackFunction = BUTTON_BACK_FUNCTION_BACK

        addSlide(SimpleSlide.Builder()
                .title(R.string.intro_1_title)
                .description(R.string.intro_1_description)
                .image(R.drawable.speaker_phone)
                .background(R.color.intro_1_background)
                .backgroundDark(R.color.colorPrimaryDark)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.intro_2_title)
                .description(R.string.intro_2_description)
                .image(R.drawable.dashboard)
                .background(R.color.intro_2_background)
                .backgroundDark(R.color.colorPrimaryDark)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.intro_3_title)
                .description(R.string.intro_3_description)
                .image(R.drawable.map)
                .background(R.color.intro_3_background)
                .backgroundDark(R.color.colorPrimaryDark)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.intro_4_title)
                .description(R.string.intro_4_description)
                .image(R.drawable.chat)
                .background(R.color.intro_4_background)
                .backgroundDark(R.color.colorPrimaryDark)
                .build())
    }
}
