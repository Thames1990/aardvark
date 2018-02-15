package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageView
import android.widget.TextView
import androidx.content.systemService
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.setIcon
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.buildIsOreoAndUp
import de.uni_marburg.mathematik.ds.serval.utils.setAardvarkColors
import org.jetbrains.anko.displayMetrics

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
        setAardvarkColors { text(title, description) }
        fingerprint.setIcon(
            icon = GoogleMaterial.Icon.gmd_fingerprint,
            color = Prefs.textColor,
            sizeDp = applicationContext.displayMetrics.densityDpi
        )
        Reprint.authenticate(object : AuthenticationListener {
            override fun onSuccess(moduleTag: Int) = finish()

            @Suppress("DEPRECATION")
            @SuppressLint("NewApi")
            override fun onFailure(
                failureReason: AuthenticationFailureReason?,
                fatal: Boolean,
                errorMessage: CharSequence?,
                moduleTag: Int,
                errorCode: Int
            ) {
                description.text = errorMessage

                val vibrator = systemService<Vibrator>()
                if (vibrator.hasVibrator()) {
                    if (buildIsOreoAndUp) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                500,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else vibrator.vibrate(500)
                }
            }
        })
    }
}
