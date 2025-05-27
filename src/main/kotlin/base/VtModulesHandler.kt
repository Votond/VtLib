package me.votond.vtlib.base

import kotlin.collections.iterator
import kotlin.random.Random

class VtModulesHandler(private val plugin: VtPlugin) {
    private val modules: MutableMap<String, VtModule> = mutableMapOf()

    /**
     * @return A read-only copy of module map.
     */
    fun getModules(): Map<String, VtModule> = modules.toMap()
    fun getModule(uniqueName: String) = modules[uniqueName]!!
    operator fun get(uniqueName: String) = getModule(uniqueName)

    fun loadModules() = modules.forEach { pair ->
        pair.value.onLoad()
            .also { plugin.debug { plugin.logger.info("Module '${pair.key}' loaded") } }
    }

    fun enableModules() {
        for (pair in modules) {
            with(pair.value) {
                getModuleListeners().forEach { plugin.server.pluginManager.registerEvents(it, plugin) }
                if (pair.value is CommandRegistrar)
                    (pair.value as CommandRegistrar).registerCommands()
                onEnable()
            }

            plugin.debug { plugin.logger.info("Module '${pair.key}' enabled") }
        }
    }

    fun disableModules() {
        for (pair in modules) {
            with(pair.value) {
                onDisable()
                if (pair.value is CommandRegistrar)
                    (pair.value as CommandRegistrar).unregisterCommands()
            }

            plugin.debug { plugin.logger.info("Module '${pair.key}' disabled") }
        }
    }

    fun addModule(uniqueName: String, module: VtModule) {
        modules[uniqueName] = module
        plugin.debug { plugin.logger.info("Module '$uniqueName' added") }
    }

    fun addModule(module: VtModule) {
        val moduleName = module.javaClass.simpleName + Random.nextInt().toString()
        addModule(moduleName, module)
    }

    fun injectModule(uniqueName: String, module: VtModule) {
        addModule(uniqueName, module)
        with(module) {
            onLoad()
            onEnable()
        }
        plugin.debug { plugin.logger.info("Module '$uniqueName' injected") }
    }

    fun removeModule(uniqueName: String) {
        modules[uniqueName]?.onDisable() ?: throw RuntimeException("Module with name '$uniqueName' not found.")
        modules.remove(uniqueName)
        plugin.debug { plugin.logger.info("Module '$uniqueName' removed") }
    }

    fun hasModule(moduleName: String): Boolean = modules[moduleName] != null
}