package de.uni_marburg.mathematik.ds.serval.intro

import android.os.Bundle
import android.view.View
import ca.allanwang.kau.utils.bindViewResettable
import ca.allanwang.kau.utils.scaleXY
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.IntroActivity
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/** Created by thames1990 on 05.12.17. */
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
        light.setThemeClick(Theme.LIGHT)
        dark.setThemeClick(Theme.DARK)
        amoled.setThemeClick(Theme.AMOLED)
        val currentTheme = Prefs.themeType
        if (currentTheme in 0..2) {
            themeList.forEachIndexed { index, v ->
                v.scaleXY = if (index == currentTheme) 1.6f else 0.8f
            }
        }
    }

    private fun View.setThemeClick(theme: Theme) {
        setOnClickListener { v ->
            Prefs.themeType = theme.ordinal
            (activity as IntroActivity).apply {
                ripple.ripple(
                    color = Prefs.backgroundColor,
                    startX = v.x + v.pivotX,
                    startY = v.y + v.pivotY
                )
                theme()
            }
            themeList.forEach { it.animate().scaleXY(if (it == this) 1.6f else 0.8f).start() }
        }
    }
}