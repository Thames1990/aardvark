package de.uni_marburg.mathematik.ds.serval.view.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import de.uni_marburg.mathematik.ds.serval.R
import kotlinx.android.synthetic.main.activity_fingerprint.*

class FingerprintActivity : AppCompatActivity() {

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

    override fun onBackPressed() = finishAffinity()
}
