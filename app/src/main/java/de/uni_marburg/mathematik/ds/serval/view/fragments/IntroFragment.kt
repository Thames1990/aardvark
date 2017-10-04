package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.withArguments
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R

/** Represents a slide in the alternative slider intro. */
class IntroFragment : Fragment() {

    private var backgroundColor: Int = 0

    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(arguments) {
            when {
                containsKey(BACKGROUND_COLOR) && containsKey(PAGE) -> {
                    backgroundColor = getInt(BACKGROUND_COLOR)
                    page = getInt(PAGE)
                }
                else -> throw RuntimeException("Must contain keys $BACKGROUND_COLOR and $PAGE")
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val layoutResId = when (page) {
            0 -> R.layout.fragment_intro_layout_1
            1 -> R.layout.fragment_intro_layout_2
            2 -> R.layout.fragment_intro_layout_3
            3 -> R.layout.fragment_intro_layout_4
            4 -> R.layout.fragment_intro_layout_5
            else -> R.layout.fragment_placeholder
        }

        val view = layoutInflater.inflate(layoutResId, container, false)
        view.tag = page

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val background = view!!.findViewById<View>(R.id.background)
        background.setBackgroundColor(backgroundColor)
    }

    override fun onDestroy() {
        super.onDestroy()
        Aardvark.refWatcher.watch(this)
    }

    companion object {

        private val BACKGROUND_COLOR = "BACKGROUND_COLOR"

        private val PAGE = "PAGE"

        fun newInstance(backgroundColor: Int, page: Int): IntroFragment {
            return IntroFragment().withArguments(
                    Pair(BACKGROUND_COLOR, backgroundColor),
                    Pair(PAGE, page)
            )
        }
    }

}
