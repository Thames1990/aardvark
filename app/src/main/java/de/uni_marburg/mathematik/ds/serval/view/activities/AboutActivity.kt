package de.uni_marburg.mathematik.ds.serval.view.activities

import ca.allanwang.kau.about.AboutActivityBase
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.iitems.CardIItem
import com.mikepenz.fastadapter.IItem
import de.uni_marburg.mathematik.ds.serval.R

class AboutActivity : AboutActivityBase(R.string::class.java, {
    cutoutDrawableRes = R.drawable.aardvark
    textColor = 0xff000000.toInt()
    backgroundColor = 0xfffafafa.toInt()
    accentColor = 0xff40c4ff.toInt()
    cutoutForeground = 0xff18FFFF.toInt()
    faqXmlRes = R.xml.faq
    faqParseNewLine = false
}) {

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        adapter.add(CardIItem {
            titleRes = R.string.faq_title
            descRes = R.string.faq_description
        })
    }
}