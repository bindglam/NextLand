package io.github.bindglam.nextland.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.github.bindglam.nextland.ChunkPos
import io.github.bindglam.nextland.Land
import io.github.bindglam.nextland.LandManager
import io.github.bindglam.nextland.NextLand
import io.github.bindglam.nextland.events.LandCreateEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.block.BlockType
import org.bukkit.entity.Player
import java.text.DecimalFormat


object LandCommand {
    fun register(){
        CommandAPICommand("land")
            .withAliases("땅")
            .withSubcommands(
                CommandAPICommand("생성")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        if(LandManager.getLand(player.location) != null){
                            player.sendMessage(Component.text("다른 땅과 겹칩니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        val requirePrice = LandManager.getOwnedLands(player.uniqueId).size*10000000.0
                        if(NextLand.ECONOMY!!.getBalance(player) < requirePrice){
                            player.sendMessage(Component.text("땅을 생성하는데 필요한 소지금이 부족합니다! ( 필요 금액: ${DecimalFormat().format(requirePrice.toLong())}원 )").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        if(NextLand.INSTANCE.config.getStringList("banned.worlds").contains(player.world.name)) {
                            player.sendMessage(Component.text("여기에는 땅을 생성할 수 없습니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        for(blockID in NextLand.INSTANCE.config.getStringList("banned.blocks")) {
                            val type = Registry.BLOCK.getOrThrow(NamespacedKey.minecraft(blockID))

                            if(player.chunk.contains(type.createBlockData())) {
                                if(type == BlockType.BEDROCK && player.world.environment == World.Environment.NORMAL) continue

                                player.sendMessage(Component.text("금지된 블록을 감지했습니다!").color(NamedTextColor.RED))
                                return@PlayerCommandExecutor
                            }
                        }

                        player.sendMessage(Component.text("정말 생성하시겠습니까? ").color(NamedTextColor.WHITE)
                            .append(Component.text("[ 예 ]").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).clickEvent(ClickEvent.callback { audience ->
                                val plr = audience as Player

                                NextLand.ECONOMY!!.withdrawPlayer(plr, requirePrice)
                                LandManager.lands.add(Land(plr.uniqueId, ChunkPos(plr.world, plr.location.chunk.x, plr.location.chunk.z)))
                                plr.sendMessage(Component.text("성공적으로 땅을 생성하였습니다!").color(NamedTextColor.GREEN))

                                LandCreateEvent(plr, ChunkPos(plr.world, plr.location.chunk.x, plr.location.chunk.z)).callEvent()
                            }))
                            .append(Component.text(" [ 아니오 ]").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                    }),
                CommandAPICommand("목록")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        player.sendMessage(Component.text(" - 땅 목록 -").color(NamedTextColor.WHITE))
                        for((i, land) in LandManager.lands.withIndex()){
                            if(LandManager.isOwnerOrAdmin(land, player.uniqueId)){
                                player.sendMessage(Component.text("${i}번 - ").color(NamedTextColor.WHITE)
                                    .append(Component.text("월드 : ${land.pos.world.name}, ").color(NamedTextColor.GREEN))
                                    .append(Component.text("X : ${land.pos.location.x}, ").color(NamedTextColor.RED))
                                    .append(Component.text("Z : ${land.pos.location.z}").color(NamedTextColor.BLUE))
                                )
                            }
                        }
                    }),
                CommandAPICommand("삭제")
                    .withArguments(IntegerArgument("번호").replaceSuggestions(ArgumentSuggestions.strings {
                        LandManager.getOwnedLandNums(it.sender)
                    }))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val number = args["번호"] as Int

                        if(LandManager.lands.size <= number){
                            player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        val land = LandManager.lands[number]
                        if(land.owner != player.uniqueId){
                            player.sendMessage(Component.text("당신은 땅의 소유자가 아닙니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        LandManager.lands.remove(land)
                        player.sendMessage(Component.text("성공적으로 땅을 삭제하였습니다!").color(NamedTextColor.YELLOW))
                    }),
                CommandAPICommand("이름변경")
                    .withArguments(IntegerArgument("번호").replaceSuggestions(ArgumentSuggestions.strings {
                        LandManager.getOwnedLandNums(it.sender)
                    }), GreedyStringArgument("이름"))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val number = args["번호"] as Int
                        val name = args["이름"] as String

                        if(LandManager.lands.size <= number){
                            player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        val land = LandManager.lands[number]
                        if(land.owner != player.uniqueId){
                            player.sendMessage(Component.text("당신은 땅의 소유자가 아닙니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        land.name = name
                        player.sendMessage(Component.text("성공적으로 땅의 이름을 바꾸었습니다!").color(NamedTextColor.GREEN))
                    }),
                CommandAPICommand("정보")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val format = DecimalFormat("###,###")

                        val requirePrice = LandManager.getOwnedLands(player.uniqueId).size*10000000.0
                        player.sendMessage(Component.text("다음 땅을 생성하는데 필요한 금액 : ").color(NamedTextColor.YELLOW)
                            .append(Component.text("${format.format(requirePrice.toInt())}원").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)))
                        player.sendMessage(Component.text("현재 땅 갯수 : ").color(NamedTextColor.YELLOW)
                            .append(Component.text("${format.format(LandManager.getOwnedLands(player.uniqueId).size)}개").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)))
                    }),
                CommandAPICommand("관리자")
                    .withSubcommands(
                        CommandAPICommand("목록")
                            .withArguments(IntegerArgument("번호").replaceSuggestions(ArgumentSuggestions.strings {
                                LandManager.getOwnedLandNums(it.sender)
                            }))
                            .executesPlayer(PlayerCommandExecutor { player, args ->
                                val number = args["번호"] as Int

                                if(LandManager.lands.size <= number){
                                    player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }

                                val land = LandManager.lands[number]

                                player.sendMessage(Component.text(" - 관리자 목록 -").color(NamedTextColor.WHITE))
                                for(admin in land.admins){
                                    player.sendMessage(Component.text("${Bukkit.getOfflinePlayer(admin).name}").color(NamedTextColor.WHITE))
                                }
                            }),
                        CommandAPICommand("추가")
                            .withArguments(OfflinePlayerArgument("유저"), IntegerArgument("번호").replaceSuggestions(ArgumentSuggestions.strings {
                                LandManager.getOwnedLandNums(it.sender)
                            }))
                            .executesPlayer(PlayerCommandExecutor { player, args ->
                                val target = args["유저"] as OfflinePlayer
                                val number = args["번호"] as Int

                                if (LandManager.lands.size <= number) {
                                    player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }

                                val land = LandManager.lands[number]
                                if (land.admins.contains(target.uniqueId) || land.owner == target.uniqueId) {
                                    player.sendMessage(Component.text("이미 ${target.name}님은 관리자입니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }
                                land.admins.add(target.uniqueId)

                                player.sendMessage(Component.text("성공적으로 관리자로 추가하였습니다! ( ${target.name} )").color(NamedTextColor.GREEN))
                                Bukkit.getPlayer(target.uniqueId)?.sendMessage(Component.text("당신은 ${player.name}님의 땅에 초대를 받으셨습니다!").color(NamedTextColor.GREEN))
                            }),
                        CommandAPICommand("삭제")
                            .withArguments(OfflinePlayerArgument("유저"), IntegerArgument("번호").replaceSuggestions(ArgumentSuggestions.strings {
                                LandManager.getOwnedLandNums(it.sender)
                            }))
                            .executesPlayer(PlayerCommandExecutor { player, args ->
                                val target = args["유저"] as OfflinePlayer
                                val number = args["번호"] as Int

                                if(LandManager.lands.size <= number){
                                    player.sendMessage(Component.text("없는 땅 번호입니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }

                                val land = LandManager.lands[number]
                                if(!land.admins.contains(target.uniqueId)){
                                    player.sendMessage(Component.text("${target.name}님은 관리자가 아닙니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }
                                land.admins.remove(target.uniqueId)

                                player.sendMessage(Component.text("성공적으로 관리자에서 삭제하였습니다! ( ${target.name} )").color(NamedTextColor.YELLOW))
                                Bukkit.getPlayer(target.uniqueId)?.sendMessage(Component.text("당신은 ${player.name}님의 관리자 권한을 박탈 당하셨습니다.").color(NamedTextColor.RED))
                            }),
                    )
            )
            .register()
    }
}