package me.votond.vtlib.util.dsl.item

import me.votond.vtlib.base.VtPlugin
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.banner.Pattern
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

fun item(plugin: VtPlugin, material: Material, block: ItemContext.() -> Unit) = ItemContext(plugin, material).apply(block).buildItemStack()

fun menuDecorItem(plugin: VtPlugin, block: ItemContext.() -> Unit) =
    ItemContext(plugin, plugin.accentMenuDecorMaterial, displayName = " ").apply(block)
        .buildItemStack()

fun ItemContext.attributeModifier(block: ItemAttributeContext.() -> Unit) {
    attributeModifiers.add(ItemAttributeContext().apply(block))
}

fun ItemContext.fireworkProperties(block: FireworkPropertiesContext.() -> Unit) {
    fireworkProperties = FireworkPropertiesContext().apply(block)
}

fun ItemContext.bookProperties(block: BookPropertiesContext.() -> Unit) {
    bookProperties = BookPropertiesContext().apply(block)
}

fun ItemContext.mapProperties(block: MapPropertiesContext.() -> Unit) {
    mapProperties = MapPropertiesContext().apply(block)
}

fun ItemContext.potionProperties(block: PotionPropertiesContext.() -> Unit) {
    potionProperties = PotionPropertiesContext().apply(block)
}

fun ItemContext.tropicalFishProperties(block: TropicalFishPropertiesContext.() -> Unit) {
    tropicalFishProperties = TropicalFishPropertiesContext().apply(block)
}

operator fun MutableSet<Pair<*, *>>.plusAssign(value: Pair<*, *>) {
    this.add(value)
}

operator fun MutableSet<ItemFlag>.plusAssign(value: ItemFlag) {
    this.add(value)
}

operator fun MutableList<String>.plusAssign(value: String) {
    this.add(value)
}

operator fun MutableList<Pattern>.plusAssign(value: Pattern) {
    this.add(value)
}

operator fun MutableList<ItemStack>.plusAssign(value: ItemStack) {
    this.add(value)
}

operator fun MutableList<FireworkEffect>.plusAssign(value: FireworkEffect) {
    this.add(value)
}

operator fun MutableList<NamespacedKey>.plusAssign(value: NamespacedKey) {
    this.add(value)
}

operator fun MutableList<PotionEffect>.plusAssign(value: PotionEffect) {
    this.add(value)
}