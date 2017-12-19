package de.uni_marburg.mathematik.ds.serval.utils

import android.content.Context
import android.support.annotation.StringRes
import ca.allanwang.kau.email.EmailBuilder
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.xml.showChangelog
import com.afollestad.materialdialogs.MaterialDialog
import de.uni_marburg.mathematik.ds.serval.R

fun Context.aardvarkChangelog() = showChangelog(R.xml.changelog, Prefs.textColor) {
    theme()
}

fun Context.materialDialogThemed(action: MaterialDialog.Builder.() -> Unit): MaterialDialog {
    val builder = MaterialDialog.Builder(this).theme()
    builder.action()
    return builder.show()
}

inline fun Context.sendAardvarkEmail(
        @StringRes subjectId: Int,
        crossinline builder: EmailBuilder.() -> Unit
) = sendAardvarkEmail(string(subjectId), builder)

inline fun Context.sendAardvarkEmail(
        subject: String,
        crossinline builder: EmailBuilder.() -> Unit
) = sendEmail(string(R.string.dev_email), subject) {
    builder()
    addItem("Random Aardvark ID", Prefs.aardvarkId)
}