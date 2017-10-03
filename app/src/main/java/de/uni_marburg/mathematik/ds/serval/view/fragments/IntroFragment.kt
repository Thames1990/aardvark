package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import java.util.*

/** Represents a slide in the alternative slider intro. */
class IntroFragment : Fragment() {

    private var backgroundColor: Int = 0

    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!arguments.containsKey(BACKGROUND_COLOR)) {
            throw RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    BACKGROUND_COLOR
            ))
        }
        backgroundColor = arguments.getInt(BACKGROUND_COLOR)

        if (!arguments.containsKey(PAGE)) {
            throw RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    PAGE
            ))
        }
        page = arguments.getInt(PAGE)
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

        val view = activity.layoutInflater.inflate(layoutResId, container, false)
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

        const val BACKGROUND_COLOR = "BACKGROUND_COLOR"

        const val PAGE = "PAGE"

        fun newInstance(backgroundColor: Int, page: Int): IntroFragment {
            val fragment = IntroFragment()
            val args = Bundle()
            args.putInt(BACKGROUND_COLOR, backgroundColor)
            args.putInt(PAGE, page)
            fragment.arguments = args
            return fragment
        }
    }

}
