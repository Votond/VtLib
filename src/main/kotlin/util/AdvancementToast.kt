package me.votond.vtlib.util

import me.votond.vtlib.base.VtPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.*

class AdvancementToast(
    private val plugin: VtPlugin,
    private val message: String,
    private val icon: Material,
    private val style: AdvancementDisplayType = AdvancementDisplayType.TASK
) {
    enum class AdvancementDisplayType {
        TASK,
        GOAL,
        CHALLENGE
    }

    private val key: NamespacedKey = UUID.randomUUID().toString().toNamespacedKey(plugin)

    private fun show(player: Player) {
        createAdvancement()
        grantAdvancement(player)

        runTaskLater(plugin, 10) {
            revokeAdvancement(player)
        }
    }

    @Suppress("DEPRECATION")
    private fun createAdvancement() {
        Bukkit.getUnsafe().loadAdvancement(
            key,
            """{
    "criteria": {
        "trigger": {
            "trigger": "minecraft:impossible"
        }
    },
    "display": {
        "icon": {
            "item": "minecraft:${icon.toString().lowercase()}"
        },
        "title": {
            "text": "$message"
        },
        "description": {
            "text": ""
        },
        "background": "minecraft:textures/gui/advancements/backgrounds/adventure.png",
        "frame": "${style.toString().lowercase()}",
        "announce_to_chat": false,
        "show_toast": true,
        "hidden": true
    },
    "requirements": [
        [
            "trigger"
        ]
    ]
}"""
        )
    }

    private fun grantAdvancement(player: Player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)!!).awardCriteria("trigger")
    }

    private fun revokeAdvancement(player: Player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)!!).revokeCriteria("trigger")
    }
}