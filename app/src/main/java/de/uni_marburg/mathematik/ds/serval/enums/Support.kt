package de.uni_marburg.mathematik.ds.serval.enums

import android.content.Context
import android.support.annotation.StringRes
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.sendAardvarkEmail

enum class Support(@StringRes val titleRes: Int) {

    FEEDBACK(titleRes = R.string.feedback),

    BUG(titleRes = R.string.bug_report),

    THEME(titleRes = R.string.theme_issue),

    FEATURE(titleRes = R.string.feature_request);

    fun sendEmail(context: Context) = with(context) {
        sendAardvarkEmail(subject = "${string(R.string.aardvark_name)}: ${string(titleRes)}") {}
    }
}