package me.votond.vtlib.util.dsl.item

import org.bukkit.Color
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect

data class PotionPropertiesContext(
    var potionEffects: MutableList<PotionEffect> = mutableListOf(),
    var basePotionData: PotionData? = null,
    var potionColor: Color? = null,
)
