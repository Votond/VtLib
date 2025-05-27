package me.votond.vtlib.util

import me.votond.vtlib.base.VtPlugin
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.resource.ResourcePackStatus
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.net.URI
import java.time.Duration
import java.util.UUID

fun Player.getEyeTargetPlayer(plugin: VtPlugin, distance: Double = 2.0): Player? {
    val fromName = name
    val fromLocation = eyeLocation
    val fromWorldName = fromLocation.world!!.name
    val fromDirection = fromLocation.direction.normalize()
    val fromVectorPos = fromLocation.toVector()

    var target: Player? = null
    var minDistance2 = Double.MAX_VALUE

    for (somePlayer in plugin.server.onlinePlayers) {
        if (somePlayer.name == fromName)
            continue

        val newTargetLocation = somePlayer.eyeLocation

        if (newTargetLocation.world!!.name != fromWorldName)
            continue

        val newTargetDistance2 = newTargetLocation.distanceSquared(fromLocation)

        if (newTargetDistance2 > distance * distance)
            continue

        val toTarget = newTargetLocation.toVector().subtract(fromVectorPos).normalize()

        val dotProduct = toTarget.dot(fromDirection)

        if (dotProduct > 0.98 && hasLineOfSight(somePlayer) && (target == null || newTargetDistance2 < minDistance2)) {
            target = somePlayer
            minDistance2 = newTargetDistance2
        }
    }

    return target
}

fun Player.getNearbyPlayers(range: Double): List<Player> {
    val players = mutableListOf<Player>()

    for (entity in getNearbyEntities(range, range, range))
        if (entity.type == EntityType.PLAYER)
            players.add(entity as Player)

    return players
}

fun Player.getNearbyPlayers(x: Double, y: Double, z: Double): List<Player> {
    val players = mutableListOf<Player>()

    for (entity in getNearbyEntities(x, y, z))
        if (entity.type == EntityType.PLAYER)
            players.add(entity as Player)

    return players
}

fun Player.sendAdventureMessage(plugin: VtPlugin, component: Component) =
    plugin.textResolver.adventure.player(this).sendMessage(component)

fun Player.sendAdventureActionBar(plugin: VtPlugin, component: Component) =
    plugin.textResolver.adventure.player(this).sendActionBar(component)

fun Player.sendAdventureTitle(
    plugin: VtPlugin,
    mainTitle: Component,
    subTitle: Component = Component.empty(),
    fadeIn: Duration = Ticks.duration(10),
    stay: Duration = Ticks.duration(70),
    fadeOut: Duration = Ticks.duration(20)
) = plugin.textResolver.adventure.player(this)
    .showTitle(Title.title(mainTitle, subTitle, Title.Times.times(fadeIn, stay, fadeOut)))

fun Player.sendAdventureBossBar(
    plugin: VtPlugin,
    name: Component,
    progress: Float = BossBar.MAX_PROGRESS,
    color: BossBar.Color = BossBar.Color.WHITE,
    overlay: BossBar.Overlay = BossBar.Overlay.PROGRESS,
    flags: Set<BossBar.Flag> = setOf()
) = plugin.textResolver.adventure.player(this)
    .showBossBar(BossBar.bossBar(name, progress, color, overlay, flags))

fun Player.openAdventureBook(
    plugin: VtPlugin,
    title: Component,
    pages: List<Component>,
    author: Component = Component.text(displayName)
) = plugin.textResolver.adventure.player(this).openBook(Book.book(title, author, pages))

fun Player.setAdventurePlayerListHeader(
    plugin: VtPlugin,
    header: Component
) = plugin.textResolver.adventure.player(this).sendPlayerListHeader(header)

fun Player.setAdventurePlayerListFooter(
    plugin: VtPlugin,
    footer: Component
) = plugin.textResolver.adventure.player(this).sendPlayerListFooter(footer)

fun Player.setAdventurePlayerListHeaderAndFooter(
    plugin: VtPlugin,
    header: Component,
    footer: Component
) = plugin.textResolver.adventure.player(this).sendPlayerListHeaderAndFooter(header, footer)

fun Player.sendAdventureResourcePack(
    plugin: VtPlugin,
    packUri: URI,
    sha1Hash: String,
    text: Component,
    isRequired: Boolean = true,
    callback: ((UUID, ResourcePackStatus, Audience) -> Unit)? = null
) {
    val packInfo = ResourcePackInfo.resourcePackInfo()
        .uri(packUri)
        .hash(sha1Hash)
        .build()

    val request = if (callback != null)
        ResourcePackRequest.resourcePackRequest()
            .packs(packInfo)
            .prompt(text)
            .required(isRequired)
            .callback(callback)
            .build()
    else
        ResourcePackRequest.resourcePackRequest()
            .packs(packInfo)
            .prompt(text)
            .required(isRequired)
            .build()

    plugin.textResolver.adventure.player(this).sendResourcePacks(request)
}

fun ConsoleCommandSender.sendAdventureMessage(plugin: VtPlugin, component: Component) =
    plugin.textResolver.adventure.sender(this).sendMessage(component)

fun String.toPermission(plugin: VtPlugin) = "${plugin.pluginName.lowercase()}.$this"

fun String.toNamespacedKey(plugin: VtPlugin) = NamespacedKey(plugin, this)

inline fun <reified T> getPersistentValue(itemMeta: ItemMeta, key: NamespacedKey): T? {
    val dataType = when {
        (T::class == Byte::class) -> PersistentDataType.BYTE
        (T::class == ByteArray::class) -> PersistentDataType.BYTE_ARRAY
        (T::class == Double::class) -> PersistentDataType.DOUBLE
        (T::class == Float::class) -> PersistentDataType.FLOAT
        (T::class == Int::class) -> PersistentDataType.INTEGER
        (T::class == IntArray::class) -> PersistentDataType.INTEGER_ARRAY
        (T::class == Long::class) -> PersistentDataType.LONG
        (T::class == LongArray::class) -> PersistentDataType.LONG_ARRAY
        (T::class == Short::class) -> PersistentDataType.SHORT
        (T::class == String::class) -> PersistentDataType.STRING

        else -> throw UnsupportedOperationException("Cannot get PersistentDataType of type ${T::class.qualifiedName}.")
    }

    return itemMeta.persistentDataContainer.get(key, dataType) as T
}

fun setPersistentValue(itemMeta: ItemMeta, key: NamespacedKey, value: Any) {
    when (value) {
        is Byte -> itemMeta.persistentDataContainer.set(key, PersistentDataType.BYTE, value)
        is ByteArray -> itemMeta.persistentDataContainer.set(key, PersistentDataType.BYTE_ARRAY, value)
        is Double -> itemMeta.persistentDataContainer.set(key, PersistentDataType.DOUBLE, value)
        is Float -> itemMeta.persistentDataContainer.set(key, PersistentDataType.FLOAT, value)
        is Int -> itemMeta.persistentDataContainer.set(key, PersistentDataType.INTEGER, value)
        is IntArray -> itemMeta.persistentDataContainer.set(key, PersistentDataType.INTEGER_ARRAY, value)
        is Long -> itemMeta.persistentDataContainer.set(key, PersistentDataType.LONG, value)
        is LongArray -> itemMeta.persistentDataContainer.set(key, PersistentDataType.LONG_ARRAY, value)
        is Short -> itemMeta.persistentDataContainer.set(key, PersistentDataType.SHORT, value)
        is String -> itemMeta.persistentDataContainer.set(key, PersistentDataType.STRING, value)

        else -> throw UnsupportedOperationException("Cannot get PersistentDataType of type ${value::class.qualifiedName}.")
    }
}

inline fun <reified T> ItemStack.getPersistentValueDirectly(key: NamespacedKey): T? {
    return getPersistentValue(this.itemMeta ?: return null, key)
}

inline fun <reified T> ItemMeta.getPersistentValueDirectly(key: NamespacedKey): T? = getPersistentValue(this, key)

fun ItemStack.setPersistentValueDirectly(key: NamespacedKey, value: Any) {
    val itemMeta = this.itemMeta!!
    setPersistentValue(itemMeta, key, value)
    this.itemMeta = itemMeta
}

fun ItemMeta.setPersistentValueDirectly(key: NamespacedKey, value: Any) = setPersistentValue(this, key, value)

fun Event.call() = Bukkit.getServer().pluginManager.callEvent(this)

private fun replacement(plugin: VtPlugin, string: String): String = string.replace("<prefix>", plugin.prefixString)

fun String.legacy(plugin: VtPlugin, replacements: Boolean = true): String {
    val string = if (replacements) replacement(plugin, this) else this
    return ChatColor.translateAlternateColorCodes('&', string)
}

fun String.colorize(plugin: VtPlugin, replacements: Boolean = true): Component {
    val string = if (replacements) replacement(plugin, this) else this
    return plugin.textResolver.formatDeserializer.deserialize(string)
}

/**
 * @param mapping map of placeholder name to its value
 */
fun String.replacePlaceholders(mapping: Map<String, String>): String {
    fun String.toConfigPlaceholder() = "{$this}"
    var transformingString = this
    mapping.forEach {
        transformingString = transformingString.replace(it.key.toConfigPlaceholder(), it.value)
    }
    return transformingString
}

fun String.replacePlaceholder(placeholderName: String, stringToReplace: String) = this.replacePlaceholders(mapOf(placeholderName to stringToReplace))