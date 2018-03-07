package de.uni_marburg.mathematik.ds.serval.views

import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event

class MapIItem(val event: Event) : KauIItem<MapIItem, MapIItem.ViewHolder>(
    layoutRes = R.layout.iitem_map,
    viewHolder = ::ViewHolder,
    type = R.id.item_map
), ThemableIItem by ThemableIItemDelegate() {

    companion object {
        const val MAP_ZOOM = 15f
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        with(holder.map) {
            onCreate(null)
            getMapAsync { googleMap ->
                with(googleMap) {
                    with(uiSettings) {
                        isClickable = false
                        isMapToolbarEnabled = false
                        setAllGesturesEnabled(false)
                    }

                    val position: LatLng = event.position
                    val marker: MarkerOptions = MarkerOptions().position(position)
                    addMarker(marker)

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM)
                    moveCamera(cameraUpdate)
                }
            }
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.map.onDestroy()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val map: MapView by bindView(R.id.map)
    }
}