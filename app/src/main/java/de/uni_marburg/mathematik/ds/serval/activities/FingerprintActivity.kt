package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.bindView
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.setAardvarkColors

class FingerprintActivity : BaseActivity() {

    private val description: TextView by bindView(R.id.description)
    private val fingerprint: ImageView by bindView(R.id.fingerprint)
    private val title: TextView by bindView(R.id.title)

    override fun backConsumer(): Boolean {
        finishAffinity()
        return true
    }

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
        setAardvarkColors { text(title, description) }
        fingerprint.setColorFilter(Prefs.textColor)
    }
}
