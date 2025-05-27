package me.votond.vtlib.util.dsl.item

import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish

data class TropicalFishPropertiesContext(
    var tropicalFishPatternColor: DyeColor? = null,
    var tropicalFishPattern: TropicalFish.Pattern? = null,
    var tropicalFishBodyColor: DyeColor? = null,
)
