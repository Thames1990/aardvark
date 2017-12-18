package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.statusBarColor
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.consume

class FingerprintActivity : BaseActivity() {

    val container: ConstraintLayout by bindView(R.id.background)
    val title: TextView by bindView(R.id.title)
    val fingerprint: ImageView by bindView(R.id.fingerprint)
    val description: TextView by bindView(R.id.description)

    override fun backConsumer(): Boolean = consume { finishAffinity() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)
        theme()
        Reprint.authenticate(object : AuthenticationListener {
            override fun onSuccess(moduleTag: Int) = finish()

            override fun onFailure(
                    failureReason: AuthenticationFailureReason?,
                    fatal: Boolean,
                    errorMessage: CharSequence?,
                    moduleTag: Int,
                    errorCode: Int
            ) {
                description.text = errorMessage
            }
        })
    }

    fun theme() {
        container.setBackgroundColor(Prefs.backgroundColor)
        title.setTextColor(Prefs.textColor)
        fingerprint.setColorFilter(Prefs.textColor)
        description.setTextColor(Prefs.textColor)
        statusBarColor = Prefs.headerColor
        navigationBarColor = Prefs.headerColor
    }
}
