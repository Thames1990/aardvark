package de.uni_marburg.mathematik.ds.serval.intro

import android.os.Bundle
import android.view.View
import ca.allanwang.kau.utils.bindViewResettable
import ca.allanwang.kau.utils.scaleXY
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.IntroActivity
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

class IntroFragmentTheme : BaseIntroFragment(R.layout.intro_theme) {

    private val light: View by bindViewResettable(R.id.intro_theme_light)
    private val dark: View by bindViewResettable(R.id.intro_theme_dark)
    private val amoled: View by bindViewResettable(R.id.intro_theme_amoled)

    private val themeList
        get() = listOf(light, dark, amoled)

    override fun viewArray(): Array<Array<out View>> =
        arrayOf(arrayOf(title), themeList.toTypedArray())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        light.setThemeClick(Themes.LIGHT)
        dark.setThemeClick(Themes.DARK)
        amoled.setThemeClick(Themes.AMOLED)

        val currentTheme = Prefs.Appearance.Theme.index
        // Check if theme is in the theme list. Currently this doesn't check for the proper theme.
        if (IntRange(0, themeList.size).contains(currentTheme)) {
            themeList.forEachIndexed { index, v ->
                v.scaleXY = if (index == currentTheme) 1.6f else 0.8f
            }
        }
    }

    private fun View.setThemeClick(theme: Themes) {
        setOnClickListener { view ->
            Prefs.Appearance.Theme.index = theme.ordinal
            val introActivity = activity as IntroActivity
            with(introActivity) {
                ripple.ripple(
                    color = Prefs.Appearance.Theme.backgroundColor,
                    startX = view.x + view.pivotX,
                    startY = view.y + view.pivotY
                )
                theme()
            }
            themeList.forEach { themeView ->
                themeView.animate().scaleXY(if (themeView == this) 1.6f else 0.8f).start()
            }
        }
    }

}