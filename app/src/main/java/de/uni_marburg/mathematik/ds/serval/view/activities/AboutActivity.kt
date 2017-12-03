package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import ca.allanwang.kau.about.AboutActivityBase
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.iitems.CardIItem
import com.mikepenz.fastadapter.IItem
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.util.setCurrentScreen
import org.jetbrains.anko.toast

class AboutActivity : AboutActivityBase(R.string::class.java, {
    textColor = Preferences.colorText
    accentColor = Preferences.colorAccent
    backgroundColor = Preferences.colorBackground
    cutoutForeground = Preferences.colorPrimary
    cutoutDrawableRes = R.drawable.aardvark
    faqPageTitleRes = R.string.faq_title
    faqXmlRes = R.xml.faq
    faqParseNewLine = false
}) {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentScreen()
    }

    var lastClick = -1L
    var clickCount = 0

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        adapter.add(CardIItem {
            titleRes = R.string.about_title
            descRes = R.string.about_description
        })
        adapter.withOnClickListener { _, _, item, _ ->
            if (item is CardIItem) {
                val now = System.currentTimeMillis()
                if (now - lastClick > 500) clickCount = 0
                else clickCount++
                lastClick = now
                if (clickCount == 7 && !Preferences.debugSettings) {
                    Preferences.debugSettings = true
                    toast(R.string.debug_enabled)
                }
            }
            false
        }
    }
}