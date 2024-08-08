package io.github.bindglam.nextland.listeners

import io.github.bindglam.nextland.LandManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent

class BlockListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onFlow(event: BlockFromToEvent){
        val block = event.toBlock
        if(block.type.isSolid) return

        val land = LandManager.getLand(block.location) ?: return
        for(x in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(x, 0, 0).x == block.x && chunk.getBlock(x, 0, 0).z == block.z){
                event.isCancelled = true
            }
        }
        for(z in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(0, 0, z).x == block.x && chunk.getBlock(0, 0, z).z == block.z){
                event.isCancelled = true
            }
        }
        for(x in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(x, 0, 15).x == block.x && chunk.getBlock(x, 0, 15).z == block.z){
                event.isCancelled = true
            }
        }
        for(z in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(15, 0, z).x == block.x && chunk.getBlock(15, 0, z).z == block.z){
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
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(x, 0, 0).x == block.x && chunk.getBlock(x, 0, 0).z == block.z){
                event.isCancelled = true
            }
        }
        for(z in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(0, 0, z).x == block.x && chunk.getBlock(0, 0, z).z == block.z){
                event.isCancelled = true
            }
        }
        for(x in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(x, 0, 15).x == block.x && chunk.getBlock(x, 0, 15).z == block.z){
                event.isCancelled = true
            }
        }
        for(z in 0..15){
            val chunk = land.chunkPos.getChunk()
            if(chunk.getBlock(15, 0, z).x == block.x && chunk.getBlock(15, 0, z).z == block.z){
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