package me.votond.vtlib.text

import me.votond.vtlib.base.VtPlugin
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class VtTextResolver(plugin: VtPlugin, richTextSystem: RichTextSystem = RichTextSystem.MINIMESSAGE) {
    val adventure = BukkitAudiences.create(plugin)

    val formatDeserializer = when(richTextSystem) {
        RichTextSystem.MINIMESSAGE -> object : FormatDeserializer {
            private val mm = MiniMessage.miniMessage()
            override fun deserialize(string: String): Component {
                return mm.deserialize(string)
            }
        }
        RichTextSystem.LEGACY -> object : FormatDeserializer {
            private val legacy = LegacyComponentSerializer.builder().character('&').hexColors().extractUrls().build()
            override fun deserialize(string: String): Component {
                return legacy.deserialize(string)
            }
        }
    }

    fun onDisable() = adventure.close()
}