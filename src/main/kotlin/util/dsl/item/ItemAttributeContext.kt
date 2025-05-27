package me.votond.vtlib.util.dsl.item

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot

data class ItemAttributeContext(
    var attribute: Attribute? = null,
    var amount: Double? = null,
    var operation: AttributeModifier.Operation? = null,
    var slot: EquipmentSlot? = null,
)