package de.uni_marburg.mathematik.ds.serval.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.support.annotation.StringRes
import androidx.content.systemService
import ca.allanwang.kau.email.EmailBuilder
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.isFinishing
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.xml.showChangelog
import com.afollestad.materialdialogs.MaterialDialog
import de.uni_marburg.mathematik.ds.serval.R

inline val Context.hasLocationPermission: Boolean
    get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.aardvarkChangelog() = showChangelog(R.xml.changelog, Prefs.textColor) { theme() }

inline fun Context.materialDialogThemed(action: MaterialDialog.Builder.() -> Unit): MaterialDialog {
    val builder = MaterialDialog.Builder(this).theme()
    builder.action()
    if (isFinishing) return builder.build()
    return builder.show()
}

inline fun Context.sendAardvarkEmail(
    @StringRes subjectId: Int,
    crossinline builder: EmailBuilder.() -> Unit
) = sendAardvarkEmail(string(subjectId), builder)

inline fun Context.sendAardvarkEmail(
    subject: String,
    crossinline builder: EmailBuilder.() -> Unit
) = sendEmail(string(R.string.developer_email_aardvark), subject) {
    builder()
    addItem(string(R.string.aardvark_id), Settings.Secure.ANDROID_ID)
}

/**
 * Vibrates once for the specified period of [milliseconds] at the specified [amplitude],
 * and then stop, if a vibrator is present on the device.
 */
@Suppress("DEPRECATION")
@SuppressLint("NewApi")
fun Context.vibrate(milliseconds: Long = 500, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    val vibrator = systemService<Vibrator>()
    if (vibrator.hasVibrator()) {
        if (buildIsOreoAndUp) {
            val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator.vibrate(vibrationEffect)
        } else vibrator.vibrate(milliseconds)
    }
}