package de.uni_marburg.mathematik.ds.serval.views

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R

class SmallHeaderIItem(
    text: String? = null,
    var textRes: Int = -1
) : KauIItem<SmallHeaderIItem, SmallHeaderIItem.ViewHolder>(
    layoutRes = R.layout.iitem_header,
    viewHolder = ::ViewHolder,
    type = R.id.item_small_header
), ThemableIItem by ThemableIItemDelegate() {

    var text: String = text ?: "Header Placeholder"

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.text.text = holder.itemView.context.string(textRes, text)
        bindTextColor(holder.text)
        bindBackgroundColor(holder.container)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.text.text = null
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView by bindView(R.id.kau_header_text)
        val container: CardView by bindView(R.id.kau_header_container)
    }

}