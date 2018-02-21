package de.uni_marburg.mathematik.ds.serval.utils

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

fun RecyclerView.withDividerItemDecoration(orientation: Int = LinearLayoutManager.VERTICAL) {
    assert(orientation == LinearLayoutManager.VERTICAL || orientation == LinearLayoutManager.HORIZONTAL)
    val dividerItemDecoration = DividerItemDecoration(context, orientation)
    addItemDecoration(dividerItemDecoration)
}