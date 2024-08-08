package io.github.bindglam.nextland

import io.github.bindglam.nextland.utils.ChunkPos
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.collections.ArrayList

class LandManager {
    companion object{
        private val landsFile = File("plugins/NextLand/lands.yml")

        private lateinit var landsConfig: YamlConfiguration

        val lands = ArrayList<Land>()

        fun init(){
            if(!landsFile.parentFile.exists())
                landsFile.parentFile.mkdir()

            if(!landsFile.exists()){
                landsFile.createNewFile()
                NextLand.LOGGER.info(Component.text("lands.yml 파일을 생성했습니다!"))
            }

            landsConfig = YamlConfiguration.loadConfiguration(landsFile)

            if(landsConfig.get("LandCounts") == null) return

            NextLand.LOGGER.info(Component.text("땅 로드중..."))
            for(i in 0..landsConfig.getInt("LandCounts")){
                val owner = UUID.fromString(landsConfig.getString("Lands.$i.owner"))
                val chunkPos = ChunkPos.get(landsConfig.getString("Lands.$i.chunkPos")!!)
                val name = landsConfig.getString("Lands.$i.name")!!

                val landers = ArrayList<UUID>()
                landsConfig.getStringList("Lands.$i.landers").forEach { strUUID ->
                    landers.add(UUID.fromString(strUUID))
                }

                lands.add(Land(owner, chunkPos).apply {
                    this.landers = landers
                    this.name = name
                })
            }
        }

        fun save(){
            NextLand.LOGGER.info(Component.text("땅 저장중..."))
            for((i, land) in lands.withIndex()){
                landsConfig.set("Lands.$i.owner", land.owner.toString())
                landsConfig.set("Lands.$i.chunkPos", land.chunkPos.toString())
                landsConfig.set("Lands.$i.name", land.name)

                val strUUIDs = ArrayList<String>()
                for(uuid in land.landers) strUUIDs.add(uuid.toString())
                landsConfig.set("Lands.$i.landers", strUUIDs)
            }
            landsConfig.set("LandCounts", lands.size-1)
            landsConfig.save(landsFile)
        }

        fun getLand(location: Location): Land? {
            for(land in lands){
                if(land.chunkPos.getChunk() == location.chunk){
                    return land
                }
            }
            return null
        }

        fun isOwnerOrLander(land: Land, uuid: UUID): Boolean {
            if(land.owner == uuid) return true

            for(lander in land.landers){
                if(lander == uuid){
                    return true
                }
            }
            return false
        }

        fun getLandsOwned(uuid: UUID): List<Land> {
            val ls = arrayListOf<Land>()
            for(land in lands){
                if(land.owner == uuid){
                    ls.add(land)
                }
            }
            return ls
        }

        fun getOwnedLandNums(uuid: UUID): Array<String?> {
            val numbers = arrayOfNulls<String>(getLandsOwned(uuid).size)
            for((i, land) in getLandsOwned(uuid).withIndex()){
                numbers[i] = lands.indexOf(land).toString()
            }
            return numbers
        }

        fun getOwnedLandNums(sender: CommandSender): Array<String?> {
            if(sender !is Player) return arrayOf()
            return getOwnedLandNums(sender.uniqueId)
        }
    }
}