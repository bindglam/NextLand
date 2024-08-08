package io.github.bindglam.nextland.commands

import io.github.bindglam.nextland.Land
import io.github.bindglam.nextland.LandManager
import io.github.bindglam.nextland.NextLand
import io.github.bindglam.nextland.events.LandCreateEvent
import io.github.bindglam.nextland.utils.ChunkPos
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class LegacyLandCommand : CommandExecutor, TabCompleter {
    override fun onCommand(player: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(player !is Player || !command.label.equals("land", true) || args.isEmpty()){
            player.sendMessage(Component.text("알맞은 사용법이 아닙니다!").color(NamedTextColor.RED))
            return false
        }

        /*
        /땅 생성
        /땅 목록
        /땅 정보
        /땅 삭제 <번호>
        /땅 땅원 추가 <이름> <번호>
        /땅 땅원 삭제 <이름> <번호>
        /땅 땅원 목록 <번호>
        /땅 이름변경 <이름> <번호>
         */

        when(args[0]){
            "생성" -> {
                if(LandManager.getLand(player.location) != null){
                    player.sendMessage(Component.text("다른 땅과 겹칩니다!").color(NamedTextColor.RED))
                    return false
                }

                val requirePrice = LandManager.getLandsOwned(player.uniqueId).size*10000000.0
                if(NextLand.ECONOMY!!.getBalance(player) < requirePrice){
                    player.sendMessage(Component.text("땅을 생성하는데 필요한 소지금이 부족합니다! ( 필요 금액: ${requirePrice.toInt()}원 )").color(NamedTextColor.RED))
                    return false
                }

                if(player.world.name != "playworld"){
                    player.sendMessage(Component.text("땅은 오직 전용 월드에서만 생성 가능합니다!").color(NamedTextColor.RED))
                    return false
                }

                if(args.size >= 2 && args[1].equals("--확인", true)) {
                    NextLand.ECONOMY!!.withdrawPlayer(player, requirePrice)
                    LandManager.lands.add(Land(player.uniqueId, ChunkPos(player.world, player.location.chunk.x, player.location.chunk.z)))
                    player.sendMessage(Component.text("성공적으로 땅을 생성하였습니다!").color(NamedTextColor.GREEN))

                    LandCreateEvent(player, ChunkPos(player.world, player.location.chunk.x, player.location.chunk.z)).callEvent()
                } else {
                    player.sendMessage(Component.text("정말 생성하시겠습니까? ").color(NamedTextColor.WHITE)
                        .append(Component.text("[ 예 ]").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).clickEvent(ClickEvent.runCommand("/땅 생성 --확인")))
                        .append(Component.text(" [ 아니오 ]").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                }
            }

            "목록" -> {
                player.sendMessage(Component.text(" - 땅 목록 -").color(NamedTextColor.WHITE))
                for((i, land) in LandManager.lands.withIndex()){
                    if(LandManager.isOwnerOrLander(land, player.uniqueId)){
                        player.sendMessage(Component.text("${i}번 - ").color(NamedTextColor.WHITE)
                            //.append(Component.text("월드 : ${land.chunkPos.world.name}, ").color(NamedTextColor.GREEN))
                            .append(Component.text("X : ${land.chunkPos.getChunk().getBlock(0, 0, 0).x}, ").color(NamedTextColor.RED))
                            .append(Component.text("Z : ${land.chunkPos.getChunk().getBlock(0, 0, 0).z}").color(NamedTextColor.BLUE))
                        )
                    }
                }
            }

            "삭제" -> {
                if(args.size < 2){
                    player.sendMessage(Component.text("알맞지 않는 사용법입니다!").color(NamedTextColor.RED))
                    return false
                }

                val number: Int
                try{
                    number = args[1].toInt()
                } catch (e: NumberFormatException){
                    player.sendMessage(Component.text("숫자가 아닙니다!").color(NamedTextColor.RED))
                    return false
                }

                if(LandManager.lands.size <= number){
                    player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                    return false
                }

                val land = LandManager.lands[number]
                if(land.owner != player.uniqueId){
                    player.sendMessage(Component.text("당신은 땅의 소유자가 아닙니다!").color(NamedTextColor.RED))
                    return false
                }

                LandManager.lands.remove(land)
                player.sendMessage(Component.text("성공적으로 땅을 삭제하였습니다!").color(NamedTextColor.YELLOW))
            }

            "이름변경" -> {
                if(args.size < 3){
                    player.sendMessage(Component.text("알맞지 않는 사용법입니다!").color(NamedTextColor.RED))
                    return false
                }

                val name = args[1].replace("_", " ")
                val number: Int
                try{
                    number = args[2].toInt()
                } catch (e: NumberFormatException){
                    player.sendMessage(Component.text("숫자가 아닙니다!").color(NamedTextColor.RED))
                    return false
                }

                if(LandManager.lands.size <= number){
                    player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                    return false
                }

                val land = LandManager.lands[number]
                if(land.owner != player.uniqueId){
                    player.sendMessage(Component.text("당신은 땅의 소유자가 아닙니다!").color(NamedTextColor.RED))
                    return false
                }

                land.name = name
                player.sendMessage(Component.text("성공적으로 땅의 이름을 바꾸었습니다!").color(NamedTextColor.GREEN))
            }

            "정보" -> {
                val requirePrice = LandManager.getLandsOwned(player.uniqueId).size*10000000.0
                player.sendMessage(Component.text("다음 땅을 생성하는데 필요한 금액 : ").color(NamedTextColor.YELLOW)
                    .append(Component.text("${requirePrice.toInt()}원").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)))
                player.sendMessage(Component.text("현재 땅 갯수 : ").color(NamedTextColor.YELLOW)
                    .append(Component.text("${LandManager.getLandsOwned(player.uniqueId).size}개").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)))
            }

            "땅원" -> {
                if(args.size < 3){
                    player.sendMessage(Component.text("알맞지 않는 사용법입니다!").color(NamedTextColor.RED))
                    return false
                }

                when(args[1]){
                    "목록" -> {
                        val number: Int
                        try{
                            number = args[2].toInt()
                        } catch (e: NumberFormatException){
                            player.sendMessage(Component.text("숫자가 아닙니다!").color(NamedTextColor.RED))
                            return false
                        }

                        if(LandManager.lands.size <= number){
                            player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                            return false
                        }

                        val land = LandManager.lands[number]

                        player.sendMessage(Component.text(" - 땅원 목록 -").color(NamedTextColor.WHITE))
                        for(lander in land.landers){
                            player.sendMessage(Component.text("${Bukkit.getOfflinePlayer(lander).name}").color(NamedTextColor.WHITE))
                        }
                    }

                    "추가" -> {
                        if (args.size < 4) {
                            player.sendMessage(Component.text("알맞지 않는 사용법입니다!").color(NamedTextColor.RED))
                            return false
                        }

                        val target = Bukkit.getOfflinePlayer(args[2])
                        val number: Int
                        try {
                            number = args[3].toInt()
                        } catch (e: NumberFormatException) {
                            player.sendMessage(Component.text("숫자가 아닙니다!").color(NamedTextColor.RED))
                            return false
                        }

                        if (LandManager.lands.size <= number) {
                            player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                            return false
                        }

                        val land = LandManager.lands[number]
                        if (land.landers.contains(target.uniqueId) || land.owner == target.uniqueId) {
                            player.sendMessage(Component.text("이미 ${target.name}님은 땅원입니다!").color(NamedTextColor.RED))
                            return false
                        }
                        land.landers.add(target.uniqueId)
                        player.sendMessage(
                            Component.text("성공적으로 땅원에 추가하였습니다! ( ${target.name} )").color(NamedTextColor.GREEN)
                        )

                        Bukkit.getPlayer(target.uniqueId)?.sendMessage(
                            Component.text("당신은 ${player.name}님의 땅에 초대를 받으셨습니다!").color(NamedTextColor.GREEN)
                        )
                    }

                    "삭제" -> {
                        if(args.size < 4){
                            player.sendMessage(Component.text("알맞지 않는 사용법입니다!").color(NamedTextColor.RED))
                            return false
                        }

                        val target = Bukkit.getOfflinePlayer(args[2])
                        val number: Int
                        try{
                            number = args[3].toInt()
                        } catch (e: NumberFormatException){
                            player.sendMessage(Component.text("숫자가 아닙니다!").color(NamedTextColor.RED))
                            return false
                        }

                        if(LandManager.lands.size <= number){
                            player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                            return false
                        }

                        val land = LandManager.lands[number]
                        if(!land.landers.contains(target.uniqueId)){
                            player.sendMessage(Component.text("${target.name}님은 땅원이 아닙니다!").color(NamedTextColor.RED))
                            return false
                        }
                        land.landers.remove(target.uniqueId)
                        player.sendMessage(Component.text("성공적으로 땅원에서 삭제하였습니다! ( ${target.name} )").color(NamedTextColor.YELLOW))
                        Bukkit.getPlayer(target.uniqueId)?.sendMessage(
                            Component.text("당신은 ${player.name}님의 땅에서 추방당하셨습니다!").color(NamedTextColor.RED)
                        )
                    }
                }
            }
        }
        return true
    }

    override fun onTabComplete(player: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        if(player !is Player || !command.label.equals("land", true)) return null

        if(args.size == 1){
            return listOf("생성", "삭제", "목록", "땅원", "이름변경", "정보")
        }

        when(args[0]){
            "삭제" -> {
                val numbers = ArrayList<String>()
                for(land in LandManager.getLandsOwned(player.uniqueId)){
                    numbers.add(LandManager.lands.indexOf(land).toString())
                }
                return numbers
            }

            "이름변경" -> {
                if(args.size == 3){
                    val numbers = ArrayList<String>()
                    for(land in LandManager.getLandsOwned(player.uniqueId)){
                        numbers.add(LandManager.lands.indexOf(land).toString())
                    }
                    return numbers
                }
                return listOf("${player.name}님의_땅", "...")
            }

            "땅원" -> {
                when(args.size){
                    2 -> return listOf("추가", "삭제", "목록")
                    3 -> {
                        when(args[1]){
                            "추가" -> {
                                val playerNames = ArrayList<String>()
                                Bukkit.getOfflinePlayers().forEach { offlinePlayer ->
                                    if(offlinePlayer.name != null)
                                        playerNames.add(offlinePlayer.name!!)
                                }
                            }

                            "삭제" -> {
                                val playerNames = ArrayList<String>()
                                Bukkit.getOfflinePlayers().forEach { offlinePlayer ->
                                    if(offlinePlayer.name != null)
                                        playerNames.add(offlinePlayer.name!!)
                                }
                            }
                        }
                    }
                    4 -> {
                        val numbers = ArrayList<String>()
                        for(land in LandManager.getLandsOwned(player.uniqueId)){
                            numbers.add(LandManager.lands.indexOf(land).toString())
                        }
                        return numbers
                    }
                }
            }
        }
        return null
    }
}