package io.github.bindglam.nextland

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.bindglam.nextland.commands.LandCommand
import io.github.bindglam.nextland.listeners.BlockListener
import io.github.bindglam.nextland.listeners.EntityListener
import io.github.bindglam.nextland.listeners.PlayerListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

class NextLand : JavaPlugin() {
    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this))

        LandCommand.register()
    }

    override fun onEnable() {
        CommandAPI.onEnable()

        saveDefaultConfig()

        INSTANCE = this
        LOGGER = componentLogger

        if (!setupEconomy() ) {
            LOGGER.info(Component.text("잉! Vault가 없다능!"))
            server.pluginManager.disablePlugin(this)
            return
        }

        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(BlockListener(), this)
        server.pluginManager.registerEvents(EntityListener(), this)

        LandManager.init()
    }

    override fun onDisable() {
        CommandAPI.onDisable()

        LandManager.save()
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        ECONOMY = rsp.provider
        return true
    }

    companion object{
        lateinit var INSTANCE: NextLand
        lateinit var LOGGER: ComponentLogger

        var ECONOMY: Economy? = null
    }
}