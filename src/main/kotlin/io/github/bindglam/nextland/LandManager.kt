package io.github.bindglam.nextland

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.collections.ArrayList

class LandManager {
    companion object {
        private val landsFile = File("plugins/NextLand/lands.yml")

        private lateinit var landsConfig: YamlConfiguration

        val lands = ArrayList<Land>()

        fun init() {
            Class.forName("io.github.bindglam.nextland.Land")
            Class.forName("io.github.bindglam.nextland.ChunkPos")

            if(!landsFile.parentFile.exists())
                landsFile.parentFile.mkdir()
            if(!landsFile.exists()){
                landsFile.createNewFile()
                NextLand.LOGGER.info(Component.text("lands.yml 파일을 생성했습니다!"))
            }

            landsConfig = YamlConfiguration.loadConfiguration(landsFile)
            if(landsConfig.get("lands.0") == null) return

            NextLand.LOGGER.info(Component.text("땅 로드중..."))

            for(i in 0..9999){
                if(landsConfig.get("lands.$i") == null) break

                lands.add(landsConfig.get("lands.$i") as Land)
            }
        }

        fun save() {
            NextLand.LOGGER.info(Component.text("땅 저장중..."))

            lands.withIndex().forEach { data ->
                landsConfig.set("lands.${data.index}", data.value)
            }

            landsConfig.save(landsFile)
        }

        fun getLand(location: Location): Land? {
            lands.forEach { land ->
                if(land.pos.chunk == location.chunk)
                    return land
            }
            return null
        }

        fun isOwnerOrAdmin(land: Land, uuid: UUID): Boolean {
            if(land.owner == uuid) return true

            land.admins.forEach { admin ->
                if(admin == uuid)
                    return true
            }
            return false
        }

        fun getOwnedLands(uuid: UUID): List<Land> {
            return lands.stream().filter { it.owner == uuid }.toList()
        }

        fun getOwnedLandNums(sender: CommandSender): Array<String?> {
            if(sender !is Player) return arrayOf()
            return getOwnedLands(sender.uniqueId).stream().map { lands.indexOf(it).toString() }.toArray { size -> Array(size) { "" } }
        }
    }
}