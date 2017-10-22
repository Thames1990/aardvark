package de.uni_marburg.mathematik.ds.serval.util

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import ca.allanwang.kau.xml.showChangelog
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.view.fragments.FingerprintFragment
import kotlinx.android.synthetic.main.fragment_fingerprint.*

class AuthenticationListener(val activity: MainActivity) : LifecycleObserver {

    private val fingerprintFragment: FingerprintFragment by lazy { FingerprintFragment() }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun authenticate() = with(activity.supportFragmentManager) {
        if (!fingerprintFragment.isAdded) {
            beginTransaction().add(android.R.id.content, fingerprintFragment).commit()
        }
        Reprint.authenticate(object : AuthenticationListener {
            override fun onSuccess(moduleTag: Int) {
                beginTransaction().remove(fingerprintFragment).commit()
                checkForNewVersion()
            }

            override fun onFailure(
                    failureReason: AuthenticationFailureReason?,
                    fatal: Boolean,
                    errorMessage: CharSequence?,
                    moduleTag: Int,
                    errorCode: Int
            ) {
                fingerprintFragment.description.text = errorMessage
            }
        })
    }

    private fun checkForNewVersion() {
        if (Preferences.showChangelog && Preferences.version < BuildConfig.VERSION_CODE) {
            Preferences.version = BuildConfig.VERSION_CODE
            activity.showChangelog(R.xml.changelog) {
                title(R.string.kau_changelog)
                positiveText(android.R.string.ok)
            }
        }
    }
}