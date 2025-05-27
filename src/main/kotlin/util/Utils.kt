package me.votond.vtlib.util

import me.votond.vtlib.base.VtPlugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

fun versionSpecific(vararg versions: String, ifMatched: () -> Unit) {
    for (version in versions)
        if (Bukkit.getBukkitVersion().split("-")[0] == version) {
            ifMatched()
            break
        }
}

fun versionSpecific(versions: List<String>, ifMatched: () -> Unit) {
    for (version in versions)
        if (Bukkit.getBukkitVersion().split("-")[0] == version) {
            ifMatched()
            break
        }
}

fun runTask(plugin: VtPlugin, asynchronously: Boolean = false, block: (BukkitRunnable) -> Unit) = if (asynchronously)
    object : BukkitRunnable() {
        override fun run() = block(this)
    }.runTaskAsynchronously(plugin)
else
    object : BukkitRunnable() {
        override fun run() = block(this)
    }.runTask(plugin)

fun runTaskLater(plugin: VtPlugin, delay: Long, asynchronously: Boolean = false, block: (BukkitRunnable) -> Unit) = if (asynchronously)
    object : BukkitRunnable() {
        override fun run() = block(this)
    }.runTaskLaterAsynchronously(plugin, delay)
else
    object : BukkitRunnable() {
        override fun run() = block(this)
    }.runTaskLater(plugin, delay)

fun runTaskTimer(plugin: VtPlugin, period: Long, asynchronously: Boolean = false, delay: Long = 0, block: (BukkitRunnable) -> Unit) =
    if (asynchronously)
        object : BukkitRunnable() {
            override fun run() = block(this)
        }.runTaskTimerAsynchronously(plugin, delay, period)
    else
        object : BukkitRunnable() {
            override fun run() = block(this)
        }.runTaskTimer(plugin, delay, period)