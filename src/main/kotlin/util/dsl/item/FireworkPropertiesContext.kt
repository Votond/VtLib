package me.votond.vtlib.util.dsl.item

import org.bukkit.FireworkEffect

data class FireworkPropertiesContext(
    var fireworkRocketEffects: MutableList<FireworkEffect> = mutableListOf(),
    var fireworkRocketPower: Int? = null,
)