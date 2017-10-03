package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.view.fragments.IntroFragment

/** [Adapter][FragmentPagerAdapter] for [intro slides][IntroFragment] */
class IntroAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {

    private val backgroundColors: IntArray =
            context.resources.getIntArray(R.array.intro_backgrounds)

    override fun getItem(position: Int): Fragment =
            IntroFragment.newInstance(backgroundColors[position], position)

    override fun getCount(): Int = backgroundColors.size

}
