package io.github.bindglam.nextland.listeners

import io.github.bindglam.nextland.Land
import io.github.bindglam.nextland.LandManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.time.Duration

class PlayerListener : Listener {
    private val enteredPlayers = hashMapOf<Player, Land>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onMove(event: PlayerMoveEvent){
        val player = event.player

        val land = LandManager.getLand(player.location)
        if(land == null){
            enteredPlayers.remove(player)
            return
        }

        if(!enteredPlayers.contains(player)){
            player.showTitle(Title.title(
                Component.text(land.name).color(NamedTextColor.YELLOW),
                Component.text("소유주 : ${Bukkit.getOfflinePlayer(land.owner).name}").color(NamedTextColor.GOLD),
                Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(1), Duration.ofMillis(100))
            ))
        } else if(enteredPlayers[player] != land) {
            player.showTitle(Title.title(
                Component.text(land.name).color(NamedTextColor.YELLOW),
                Component.text("소유주 : ${Bukkit.getOfflinePlayer(land.owner).name}").color(NamedTextColor.GOLD),
                Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(1), Duration.ofMillis(100))
            ))
        }
        enteredPlayers[player] = land

        for(x in 0..<16){
            land.pos.world.spawnParticle(Particle.HAPPY_VILLAGER, land.pos.location.add(x.toDouble(), player.location.y, 0.0), 1)
            land.pos.world.spawnParticle(Particle.HAPPY_VILLAGER, land.pos.location.add(x.toDouble(), player.location.y, 16.0), 1)
        }

        for(z in 0..<16){
            land.pos.world.spawnParticle(Particle.HAPPY_VILLAGER, land.pos.location.add(0.0, player.location.y, z.toDouble()), 1)
            land.pos.world.spawnParticle(Particle.HAPPY_VILLAGER, land.pos.location.add(16.0, player.location.y, z.toDouble()), 1)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockPlace(event: BlockPlaceEvent){
        val player = event.player
        val block = event.block
        if(player.isOp) return

        val land = LandManager.getLand(block.location) ?: return
        if(!LandManager.isOwnerOrAdmin(land, player.uniqueId)){
            event.isCancelled = true
            player.sendActionBar(Component.text("여기는 당신의 땅이 아닙니다!").color(NamedTextColor.RED))
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockBreak(event: BlockBreakEvent){
        val player = event.player
        val block = event.block
        if(player.isOp) return

        val land = LandManager.getLand(block.location) ?: return
        if(!LandManager.isOwnerOrAdmin(land, player.uniqueId)){
            event.isCancelled = true
            player.sendActionBar(Component.text("여기는 당신의 땅이 아닙니다!").color(NamedTextColor.RED))
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInteract(event: PlayerInteractEvent){
        val player = event.player
        val block = event.clickedBlock ?: return
        if(player.isOp) return

        val land = LandManager.getLand(block.location) ?: return
        if(!LandManager.isOwnerOrAdmin(land, player.uniqueId)){
            event.isCancelled = true
            player.sendActionBar(Component.text("여기는 당신의 땅이 아닙니다!").color(NamedTextColor.RED))
        }
    }
}