package de.uni_marburg.mathematik.ds.serval.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.StringRes
import ca.allanwang.kau.email.EmailBuilder
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.PERMISSION_WRITE_EXTERNAL_STORAGE
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.isFinishing
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.xml.showChangelog
import com.afollestad.materialdialogs.MaterialDialog
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R

inline val Context.hasLocationPermission: Boolean
    get() = hasPermission(PERMISSION_ACCESS_FINE_LOCATION)

inline val Context.hasWriteExternalStoragePermission: Boolean
    get() = hasPermission(PERMISSION_WRITE_EXTERNAL_STORAGE)

/**
 * Show the showChangelog.
 */
fun Context.showChangelog() = showChangelog(
    xmlRes = R.xml.changelog,
    textColor = Prefs.Appearance.textColor,
    customize = { theme() }
)

/**
 * Show a themed dialog.
 */
inline fun Context.materialDialogThemed(action: MaterialDialog.Builder.() -> Unit): MaterialDialog {
    val builder = MaterialDialog.Builder(this).theme()
    builder.action()
    if (isFinishing) return builder.build()
    return builder.show()
}

/**
 * Send a support email.
 */
inline fun Context.sendSupportEmail(
    @StringRes subjectId: Int,
    crossinline builder: EmailBuilder.() -> Unit
) = sendSupportEmail(subject = string(subjectId), builder = builder)

/**
 * Send a support email.
 */
inline fun Context.sendSupportEmail(
    subject: String,
    crossinline builder: EmailBuilder.() -> Unit
) = sendEmail(
    email = string(R.string.developer_email_aardvark),
    subject = subject,
    builder = {
        builder()
        addItem(string(R.string.aardvark_id), Aardvark.aardvarkId)
    }
)

/**
 * Vibrates once for the specified period of [milliseconds] at the specified [amplitude],
 * and then stop, if a vibrator is present on the device.
 */
@Suppress("DEPRECATION")
@SuppressLint("NewApi")
fun Context.vibrate(milliseconds: Long = 500, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        if (buildIsOreoAndUp) {
            val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator.vibrate(vibrationEffect)
        } else vibrator.vibrate(milliseconds)
    }
}