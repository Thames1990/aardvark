package de.uni_marburg.mathematik.ds.serval.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

class BadgedIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val badgeTextView: TextView by bindView(R.id.badge_text)
    private val badgeImage: ImageView by bindView(R.id.badge_image)

    init {
        View.inflate(context, R.layout.view_badged_icon, this)
        val badgeColor = Prefs.mainActivityLayout.backgroundColor
            .withAlpha(255)
            .colorToForeground(0.2f)
        val badgeBackground = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(badgeColor, badgeColor)
        ).apply {
            cornerRadius = 13.dpToPx.toFloat()
        }
        with(badgeTextView) {
            background = badgeBackground
            setTextColor(Prefs.mainActivityLayout.iconColor)
        }
    }

    var iicon: IIcon? = null
        set(value) {
            field = value
            badgeImage.setImageDrawable(
                value?.toDrawable(
                    context,
                    sizeDp = 20,
                    color = Prefs.mainActivityLayout.iconColor
                )
            )
        }

    var badgeText: String?
        get() = badgeTextView.text.toString()
        set(value) {
            if (badgeTextView.text == value) return
            badgeTextView.text = value
            if (value != null && value != "0") badgeTextView.visible()
            else badgeTextView.gone()
        }

}