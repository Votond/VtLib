package me.votond.vtlib.base

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import me.votond.vtlib.text.RichTextSystem
import me.votond.vtlib.text.VtTextResolver
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

abstract class VtPlugin(
    val pluginName: String,
    var debugMode: Boolean = false
) : JavaPlugin() {
    open val pluginVersion = "1.0.0"
    open val accentMenuDecorMaterial: Material = Material.RED_STAINED_GLASS_PANE
    open val prefixString: String = "<red>ᴠᴛᴘʟᴜɢɪɴ <gray>• <reset>"
    open val isPremium = true

    lateinit var modulesHandler: VtModulesHandler
        private set
    lateinit var textResolver: VtTextResolver
        private set
    open val richTextSystem = RichTextSystem.MINIMESSAGE

    open fun shutdown(msg: String) {
        logger.severe(msg)
        pluginLoader.disablePlugin(this)
        throw RuntimeException("The plugin is disabled. Check logs for details.")
    }

    inline fun debug(block: (VtPlugin) -> Unit) {
        if (debugMode)
            block(this)
    }

    final override fun onLoad() {
        saveDefaultConfig()
        modulesHandler = VtModulesHandler(this)

        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true).usePluginNamespace())
        debug { logger.info("CommandAPI loaded") }

        onPluginLoad()

        modulesHandler.loadModules()
        debug { logger.info("Modules loaded") }

        debug { logger.info("Plugin loaded") }
    }

    final override fun onEnable() {
        textResolver = VtTextResolver(this, richTextSystem)
        debug { logger.info("Text system enabled") }

        CommandAPI.onEnable()
        debug { logger.info("CommandAPI enabled") }

        onPluginEnable()

        modulesHandler.enableModules()
        debug { logger.info("Modules enabled") }

        debug { logger.info("Plugin enabled") }
    }

    final override fun onDisable() {
        modulesHandler.disableModules()
        debug { logger.info("Modules disabled") }

        onPluginDisable()

        CommandAPI.onDisable()
        debug { logger.info("CommandAPI disabled") }

        textResolver.onDisable()
        debug { logger.info("Text system disabled") }
        debug { logger.info("Plugin disabled") }
    }

    protected open fun onPluginLoad() {}
    protected open fun onPluginEnable() {}
    protected open fun onPluginDisable() {}
}