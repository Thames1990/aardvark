package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.setTextWithOptions
import de.uni_marburg.mathematik.ds.serval.utils.vibrate
import org.jetbrains.anko.find

/**
 * Show a fingerprint authentication overlay.
 */
class FingerprintActivity : AppCompatActivity() {

    private lateinit var iconCircle: CardView
    private lateinit var icon: ImageView
    private lateinit var description: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fingerprintDialog: MaterialDialog = createFingerprintDialog()
        fingerprintDialog.show()
        authenticate(fingerprintDialog)
    }

    private fun createFingerprintDialog(): MaterialDialog {
        val fingerprintDialog: MaterialDialog = MaterialDialog.Builder(this)
            .customView(R.layout.dialog_confirm_fingerprint, false)
            .canceledOnTouchOutside(false)
            .cancelable(false)
            .build()

        with(fingerprintDialog.customView!!) {
            setBackgroundColor(Prefs.backgroundColor.lighten(0.1f).withMinAlpha(200))

            val title: TextView = find(R.id.fingerprint_title)
            title.setTextColor(Prefs.textColor)

            val subTitle: TextView = find(R.id.fingerprint_subtitle)
            subTitle.setTextColor(Prefs.textColor)

            description = find(R.id.fingerprint_description)
            description.setTextColor(Prefs.textColor.adjustAlpha(0.5f))

            iconCircle = find(R.id.fingerprint_icon_circle)
            iconCircle.setCardBackgroundColor(Theme.LYNCH)

            icon = find(R.id.fingerprint_icon)
            icon.setIcon(icon = CommunityMaterial.Icon.cmd_fingerprint, color = Prefs.iconColor)

            val usePassword: Button = find(R.id.button_use_password)
            with(usePassword) {
                setTextColor(Theme.LYNCH)
                isEnabled = false
            }

            val cancel: Button = find(R.id.button_cancel)
            with(cancel) {
                setTextColor(Theme.LYNCH)
                isEnabled = false
            }
        }
        return fingerprintDialog
    }

    private fun authenticate(fingerprintDialog: MaterialDialog) {
        Reprint.authenticate(object : AuthenticationListener {
            override fun onSuccess(moduleTag: Int) {
                iconCircle.setCardBackgroundColor(Theme.PERSIAN_GREEN)
                icon.fadeScaleTransition {
                    setIcon(
                        icon = CommunityMaterial.Icon.cmd_check,
                        color = Prefs.iconColor
                    )
                }
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
                iconCircle.setCardBackgroundColor(Theme.POMEGRENADE)
                icon.setIcon(icon = CommunityMaterial.Icon.cmd_exclamation, color = Prefs.iconColor)
                description.setTextWithOptions(errorMessage.toString())
                if (Prefs.useVibrations) vibrate()
            }
        })
    }

}