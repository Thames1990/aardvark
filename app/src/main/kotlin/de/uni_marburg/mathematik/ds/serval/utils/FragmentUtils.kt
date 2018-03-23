package de.uni_marburg.mathematik.ds.serval.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.MenuRes
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import ca.allanwang.kau.utils.setMenuIcons
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs

fun Fragment.createOptionsMenu(
    inflater: MenuInflater?,
    @MenuRes menuRes: Int,
    menu: Menu?,
    @ColorRes color: Int = AppearancePrefs.Theme.iconColor,
    vararg iicons: Pair<Int, IIcon>,
    block: () -> Unit = {}
) {
    inflater ?: return
    menu ?: return
    inflater.inflate(menuRes, menu)
    val context: Context = requireContext()
    context.setMenuIcons(menu, color, *iicons)
    block()
}

inline fun <reified T : ViewModel> Fragment.getViewModel(): T =
    ViewModelProviders.of(requireActivity())[T::class.java]