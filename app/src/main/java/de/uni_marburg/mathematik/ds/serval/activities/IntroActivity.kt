package de.uni_marburg.mathematik.ds.serval.activities

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.Manifest
import android.os.Bundle
import ca.allanwang.kau.utils.colorToBackground
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import de.uni_marburg.mathematik.ds.serval.util.setCurrentScreen
import de.uni_marburg.mathematik.ds.serval.fragments.AuthenticationSlide

class IntroActivity : MaterialIntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentScreen()
        enableLastSlideAlphaExitTransition(true)
        nextButtonTranslationWrapper.setEnterTranslation { view, percentage ->
            view.alpha = percentage
        }

        addSlide(SlideFragmentBuilder()
                .title(string(R.string.intro_1_title))
                .description(string(R.string.intro_1_description))
                .image(R.drawable.speaker_phone)
                .backgroundColor(R.color.intro_1_background)
                .buttonsColor(Prefs.colorPrimary.colorToBackground())
                .build())

        addSlide(SlideFragmentBuilder()
                .title(string(R.string.intro_2_title))
                .description(string(R.string.intro_2_description))
                .image(R.drawable.dashboard)
                .backgroundColor(R.color.intro_2_background)
                .buttonsColor(Prefs.colorPrimary.colorToBackground())
                .build())

        addSlide(SlideFragmentBuilder()
                .title(string(R.string.intro_3_title))
                .description(string(R.string.intro_3_description))
                .image(R.drawable.map)
                .backgroundColor(R.color.intro_3_background)
                .buttonsColor(Prefs.colorPrimary.colorToBackground())
                .neededPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                .build())

        addSlide(SlideFragmentBuilder()
                .title(string(R.string.intro_4_title))
                .description(string(R.string.intro_4_description))
                .image(R.drawable.chat)
                .backgroundColor(R.color.intro_4_background)
                .buttonsColor(Prefs.colorPrimary.colorToBackground())
                .build())

        addSlide(AuthenticationSlide())
    }

    override fun onBackPressed() = finishAffinity()
}
