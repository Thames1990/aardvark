package de.uni_marburg.mathematik.ds.serval.view.fragments

import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.hideKeyboard
import ca.allanwang.kau.utils.toast
import ca.allanwang.kau.utils.value
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.util.consume
import kotlinx.android.synthetic.main.slide_authentication.*


class AuthenticationSlide : SlideFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Aardvark.firebaseAnalytics.setCurrentScreen(activity, this::class.java.simpleName, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        Aardvark.refWatcher.watch(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.slide_authentication, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                consume { login() }
            }
            false
        }
        password.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                consume { login() }
            }
            false
        }
        login.setOnClickListener { login() }
    }

    override fun backgroundColor(): Int = R.color.intro_5_background

    override fun buttonsColor(): Int = R.color.color_primary_dark

    override fun canMoveFurther(): Boolean = Preferences.isLoggedIn

    override fun cantMoveFurtherErrorMessage(): String = getString(R.string.login_required)

    private fun login() {
        if (!username.value.isEmpty() && !password.value.isEmpty()) {
            Preferences.kervalUser = username.value
            Preferences.kervalPassword = password.value
            Preferences.isLoggedIn = true
            Preferences.isFirstLaunch = false
            login.hideKeyboard()
            context.toast(getString(R.string.successfully_logged_in))
            activity.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KEYCODE_DPAD_RIGHT))
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