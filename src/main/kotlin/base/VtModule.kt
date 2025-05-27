package me.votond.vtlib.base

import org.bukkit.event.Listener

interface VtModule {
    fun onLoad() {}
    fun onEnable() {}
    fun onDisable() {}

    fun getModuleListeners(): List<Listener> = listOf()
}