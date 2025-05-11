package io.github.bindglam.nextland.listeners

import io.github.bindglam.nextland.LandManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*

class BlockListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onFlow(event: BlockFromToEvent){
        val block = event.toBlock
        if(block.type.isSolid) return

        val land = LandManager.getLand(block.location) ?: return

        for(x in 0..15){
            val offsetLoc1 = land.pos.location.add(x.toDouble(), 0.0, 0.0)
            val offsetLoc2 = land.pos.location.add(x.toDouble(), 0.0, 15.0)

            if(offsetLoc1.blockX == block.x && offsetLoc1.blockZ == block.z){
                event.isCancelled = true
            }
            if(offsetLoc2.blockX == block.x && offsetLoc2.blockZ == block.z){
                event.isCancelled = true
            }
        }
        for(z in 0..15){
            val offsetLoc1 = land.pos.location.add(0.0, 0.0, z.toDouble())
            val offsetLoc2 = land.pos.location.add(15.0, 0.0, z.toDouble())

            if(offsetLoc1.blockX == block.x && offsetLoc1.blockZ == block.z){
                event.isCancelled = true
            }
            if(offsetLoc2.blockX == block.x && offsetLoc2.blockZ == block.z){
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPhysics(event: BlockPhysicsEvent){
        val block = event.block
        if(block.type.isSolid) return

        val land = LandManager.getLand(block.location) ?: return

        for(x in 0..15){
            val offsetLoc1 = land.pos.location.add(x.toDouble(), 0.0, 0.0)
            val offsetLoc2 = land.pos.location.add(x.toDouble(), 0.0, 15.0)

            if(offsetLoc1.blockX == block.x && offsetLoc1.blockZ == block.z){
                event.isCancelled = true
            }
            if(offsetLoc2.blockX == block.x && offsetLoc2.blockZ == block.z){
                event.isCancelled = true
            }
        }
        for(z in 0..15){
            val offsetLoc1 = land.pos.location.add(0.0, 0.0, z.toDouble())
            val offsetLoc2 = land.pos.location.add(15.0, 0.0, z.toDouble())

            if(offsetLoc1.blockX == block.x && offsetLoc1.blockZ == block.z){
                event.isCancelled = true
            }
            if(offsetLoc2.blockX == block.x && offsetLoc2.blockZ == block.z){
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPistonExtend(event: BlockPistonExtendEvent){
        val piston = event.block
        if(LandManager.getLand(piston.location) != null) return

        for(block in event.blocks){
            if(LandManager.getLand(block.location) != null || LandManager.getLand(block.location.add(event.direction.direction)) != null){
                event.isCancelled = true
                break
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPistonRetract(event: BlockPistonRetractEvent){
        val piston = event.block
        if(LandManager.getLand(piston.location) != null) return

        for(block in event.blocks){
            if(LandManager.getLand(block.location) != null){
                event.isCancelled = true
                break
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockExplode(event: BlockExplodeEvent){
        val block = event.block
        if(LandManager.getLand(block.location) != null){
            event.isCancelled = true
        }

        val blockList = event.blockList()
        for(b in blockList){
            if(LandManager.getLand(b.location) != null){
                blockList.remove(b)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockSpread(event: BlockSpreadEvent){
        val block = event.block

        when(block.type){
            Material.FIRE -> {
                if(LandManager.getLand(block.location) != null){
                    event.isCancelled = true
                }
            }

            else -> {
            }
        }
    }
}