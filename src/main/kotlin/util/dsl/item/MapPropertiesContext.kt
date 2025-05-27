package me.votond.vtlib.util.dsl.item

import org.bukkit.Color
import org.bukkit.map.MapView

data class MapPropertiesContext(
    var mapColor: Color? = null,
    var mapLocationName: String? = null,
    var mapView: MapView? = null,
    var isMapScaling: Boolean? = null,
)