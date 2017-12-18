package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.consume
import kotlinx.android.synthetic.main.activity_fingerprint.*

class FingerprintActivity : BaseActivity() {

    override fun backConsumer(): Boolean = consume { finishAffinity() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)
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
}
