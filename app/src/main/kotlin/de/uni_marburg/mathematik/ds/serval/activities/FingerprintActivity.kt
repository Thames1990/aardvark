package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.adjustAlpha
import ca.allanwang.kau.utils.lighten
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.withMinAlpha
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.ExperimentalPrefs
import de.uni_marburg.mathematik.ds.serval.utils.setIconWithOptions
import de.uni_marburg.mathematik.ds.serval.utils.setTextWithOptions
import de.uni_marburg.mathematik.ds.serval.utils.vibrate
import org.jetbrains.anko.find

/**
 * Show a fingerprint authentication overlay.
 */
class FingerprintActivity : AppCompatActivity() {

    private lateinit var description: TextView
    private lateinit var icon: ImageView
    private lateinit var iconCircle: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fingerprintDialog: MaterialDialog = createFingerprintDialog().apply { show() }
        authenticateWith(fingerprintDialog)
    }

    private fun createFingerprintDialog(): MaterialDialog {
        val fingerprintDialog: MaterialDialog = MaterialDialog.Builder(this)
            .customView(R.layout.dialog_confirm_fingerprint, false)
            .canceledOnTouchOutside(false)
            .cancelable(false)
            .build()

        with(fingerprintDialog.customView!!) {
            setBackgroundColor(AppearancePrefs.Theme.backgroundColor.lighten(0.1f).withMinAlpha(200))

            val title: TextView = find(R.id.fingerprint_title)
            title.setTextColor(AppearancePrefs.Theme.textColor)

            val subTitle: TextView = find(R.id.fingerprint_subtitle)
            subTitle.setTextColor(AppearancePrefs.Theme.textColor)

            description = find(R.id.fingerprint_description)
            description.setTextColor(AppearancePrefs.Theme.textColor.adjustAlpha(0.5f))

            iconCircle = find(R.id.fingerprint_icon_circle)
            iconCircle.setCardBackgroundColor(Themes.LYNCH)

            icon = find(R.id.fingerprint_icon)
            icon.setIcon(
                icon = CommunityMaterial.Icon.cmd_fingerprint,
                color = AppearancePrefs.Theme.iconColor
            )

            val usePassword: Button = find(R.id.button_use_password)
            with(usePassword) {
                setTextColor(Themes.LYNCH)
                isEnabled = false
            }

            val cancel: Button = find(R.id.button_cancel)
            with(cancel) {
                setTextColor(Themes.LYNCH)
                isEnabled = false
            }
        }
        return fingerprintDialog
    }

    private fun authenticateWith(fingerprintDialog: MaterialDialog) {
        Reprint.authenticate(object : AuthenticationListener {
            override fun onSuccess(moduleTag: Int) {
                iconCircle.setCardBackgroundColor(Themes.PERSIAN_GREEN)
                icon.setIconWithOptions(
                    icon = CommunityMaterial.Icon.cmd_check,
                    color = AppearancePrefs.Theme.iconColor
                )
                description.setTextWithOptions(
                    textRes = R.string.dialog_fingerprint_recognized,
                    duration = 500L,
                    onFinish = {
                        fingerprintDialog.dismiss()
                        finish()
                    }
                )
            }

            override fun onFailure(
                failureReason: AuthenticationFailureReason?,
                fatal: Boolean,
                errorMessage: CharSequence?,
                moduleTag: Int,
                errorCode: Int
            ) {
                iconCircle.setCardBackgroundColor(Themes.POMEGRENADE)
                icon.setIconWithOptions(
                    icon = CommunityMaterial.Icon.cmd_exclamation,
                    color = AppearancePrefs.Theme.iconColor
                )
                description.setTextWithOptions(errorMessage.toString())
                if (ExperimentalPrefs.vibrationsEnabled) vibrate()
            }
        })
    }

}