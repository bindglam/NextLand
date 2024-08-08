package io.github.bindglam.nextland

import io.github.bindglam.nextland.utils.ChunkPos
import org.bukkit.Bukkit
import org.joml.Vector2i
import java.util.*

class Land(val owner: UUID, val chunkPos: ChunkPos) {
    var landers = ArrayList<UUID>()
    var name = "${Bukkit.getOfflinePlayer(owner).name}님의 땅"
}