package de.uni_marburg.mathematik.ds.serval.enums

import android.content.Context
import android.support.annotation.StringRes
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.sendSupportEmail

/**
 * Defines seperate support topics.
 *
 * @property titleRes Resource ID of the title
 */
enum class SupportTopic(@StringRes val titleRes: Int) {

    FEEDBACK(R.string.feedback),

    BUG(R.string.kau_report_bug),

    THEME(R.string.theme_issue),

    FEATURE(R.string.feature_request);

    /**
     * Sends an email with the given support topic
     */
    fun sendEmail(context: Context) = with(context) {
        sendSupportEmail(subject = "${string(R.string.aardvark_name)}: ${string(titleRes)}") {}
    }
}