package io.github.bindglam.nextland.utils

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.World

class ChunkPos(var world: World, var x: Int, var z: Int) {
    fun getChunk(): Chunk{
        return world.getChunkAt(x, z)
    }

    override fun toString(): String {
        return "world=${world.name},x=$x,z=$z"
    }

    companion object{
        fun get(data: String): ChunkPos {
            val instance = ChunkPos(Bukkit.getWorlds()[0], 0, 0)

            for(section in data.split(",")){
                val key = section.split("=")[0]
                val value = section.split("=")[1]

                when(key){
                    "world" -> instance.world = Bukkit.getWorld(value)!!
                    "x" -> instance.x = value.toInt()
                    "z" -> instance.z = value.toInt()
                }
            }
            return instance
        }
    }
}