package de.uni_marburg.mathematik.ds.serval.enums

import android.content.Context
import android.support.annotation.StringRes
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.sendAardvarkEmail

/** Created by thames1990 on 09.12.17. */
enum class Support(@StringRes val titleRes: Int) {
    FEEDBACK(R.string.feedback),
    BUG(R.string.bug_report),
    THEME(R.string.theme_issue),
    FEATURE(R.string.feature_request);

    fun sendEmail(context: Context) {
        with(context) {
            sendAardvarkEmail("${string(R.string.aardvark_name)}: ${string(titleRes)}") {}
        }
    }
}