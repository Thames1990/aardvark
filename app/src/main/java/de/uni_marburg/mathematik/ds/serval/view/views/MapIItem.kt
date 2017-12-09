package de.uni_marburg.mathematik.ds.serval.view.views

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
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.utils.MAP_ZOOM

class MapIItem(val event: Event) : KauIItem<MapIItem, MapIItem.ViewHolder>(
        R.layout.iitem_map, { ViewHolder(it) }
), ThemableIItem by ThemableIItemDelegate() {

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.map.apply {
            onCreate(null)
            getMapAsync { googleMap ->
                googleMap.apply {
                    uiSettings.apply {
                        setAllGesturesEnabled(false)
                        isMapToolbarEnabled = false
                        isClickable = false
                    }
                    val position = LatLng(event.location.latitude, event.location.longitude)
                    addMarker(MarkerOptions().position(position))
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