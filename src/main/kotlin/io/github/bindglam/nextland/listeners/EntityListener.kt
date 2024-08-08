package io.github.bindglam.nextland.listeners

import io.github.bindglam.nextland.LandManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent

class EntityListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent){
        val entity = event.entity
        val attacker = event.damager
        if(attacker !is Player) return
        val land = LandManager.getLand(attacker.location) ?: return
        if(!LandManager.isOwnerOrLander(land, attacker.uniqueId)){
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityExplode(event: EntityExplodeEvent){
        val entity = event.entity
        if(LandManager.getLand(entity.location) != null){
            event.isCancelled = true
        }

        val blockList = event.blockList()
        for(block in blockList){
            if(LandManager.getLand(block.location) != null){
                blockList.remove(block)
            }
        }
    }
}