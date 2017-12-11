package de.uni_marburg.mathematik.ds.serval.utils

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.tint
import de.uni_marburg.mathematik.ds.serval.R

fun RecyclerView.withDividerDecoration(
        context: Context,
        orientation: Int,
        color: Int = Prefs.accentColor
) {
    val divider = DividerItemDecoration(context, orientation)
    val resource = context.drawable(R.drawable.line_divider)
    DrawableCompat.wrap(resource).tint(color)
    divider.setDrawable(resource)
    addItemDecoration(divider)
}