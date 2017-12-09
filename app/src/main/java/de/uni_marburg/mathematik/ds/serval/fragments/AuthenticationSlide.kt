package de.uni_marburg.mathematik.ds.serval.fragments

import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import ca.allanwang.kau.utils.colorToBackground
import ca.allanwang.kau.utils.hideKeyboard
import ca.allanwang.kau.utils.toast
import ca.allanwang.kau.utils.value
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import de.uni_marburg.mathematik.ds.serval.util.Prefs.isFirstLaunch
import de.uni_marburg.mathematik.ds.serval.util.Prefs.isLoggedIn
import de.uni_marburg.mathematik.ds.serval.util.Prefs.kervalPassword
import de.uni_marburg.mathematik.ds.serval.util.Prefs.kervalUser
import de.uni_marburg.mathematik.ds.serval.util.consumeIf
import kotlinx.android.synthetic.main.slide_authentication.*


class AuthenticationSlide : SlideFragment() {

    override fun onDestroy() {
        super.onDestroy()
        Aardvark.refWatcher.watch(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.slide_authentication, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        password.setOnEditorActionListener { _, actionId, _ ->
            consumeIf(actionId == EditorInfo.IME_ACTION_DONE) { login() }
        }
        login.setOnClickListener { login() }
    }

    override fun backgroundColor(): Int = R.color.intro_5_background

    override fun buttonsColor(): Int = Prefs.colorPrimary.colorToBackground()

    override fun canMoveFurther(): Boolean = isLoggedIn

    override fun cantMoveFurtherErrorMessage(): String = getString(R.string.login_required)

    private fun login() {
        if (!username.value.isEmpty() && !password.value.isEmpty()) {
            // Check against default. Momentarily there's only one correct API credential.
            // Without this check the app would hang on trying to download events.
            if (username.value == kervalUser && password.value == kervalPassword) {
                kervalUser = username.value
                kervalPassword = password.value
                isLoggedIn = true
                isFirstLaunch = false

                login.hideKeyboard()

                // Move to "next" slide, finish intro
                activity!!.dispatchKeyEvent(KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DPAD_RIGHT
                ))
            } else {
                username.text.clear()
                password.text.clear()
                activity!!.toast("Incorrect login credentials")
            }
        } else {
            if (username.value.isEmpty()) {
                username_layout.error = getString(R.string.username_must_not_be_empty)
            }
            if (password.value.isEmpty()) {
                password_layout.error = getString(R.string.password_must_not_be_empty)
            }
        }
    }
}