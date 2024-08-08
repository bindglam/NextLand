package io.github.bindglam.nextland.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
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
import org.bukkit.OfflinePlayer

object LandCommand {
    fun register(){
        CommandAPICommand("land")
            .withAliases("땅")
            .withSubcommands(
                CommandAPICommand("생성")
                    .withOptionalArguments(TextArgument("confirm"))
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        if(LandManager.getLand(player.location) != null){
                            player.sendMessage(Component.text("다른 땅과 겹칩니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        val requirePrice = LandManager.getLandsOwned(player.uniqueId).size*10000000.0
                        if(NextLand.ECONOMY!!.getBalance(player) < requirePrice){
                            player.sendMessage(Component.text("땅을 생성하는데 필요한 소지금이 부족합니다! ( 필요 금액: ${requirePrice.toInt()}원 )").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        if(player.world.name != "playworld"){
                            player.sendMessage(Component.text("땅은 오직 전용 월드에서만 생성 가능합니다!").color(NamedTextColor.RED))
                            return@PlayerCommandExecutor
                        }

                        if(args["confirm"] != null && args["confirm"]!! == "--확인") {
                            NextLand.ECONOMY!!.withdrawPlayer(player, requirePrice)
                            LandManager.lands.add(Land(player.uniqueId, ChunkPos(player.world, player.location.chunk.x, player.location.chunk.z)))
                            player.sendMessage(Component.text("성공적으로 땅을 생성하였습니다!").color(NamedTextColor.GREEN))

                            LandCreateEvent(player, ChunkPos(player.world, player.location.chunk.x, player.location.chunk.z)).callEvent()
                        } else {
                            player.sendMessage(Component.text("정말 생성하시겠습니까? ").color(NamedTextColor.WHITE)
                                .append(Component.text("[ 예 ]").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).clickEvent(ClickEvent.runCommand("/땅 생성 '--확인'")))
                                .append(Component.text(" [ 아니오 ]").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                        }
                    }),
                CommandAPICommand("목록")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
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
                        val requirePrice = LandManager.getLandsOwned(player.uniqueId).size*10000000.0
                        player.sendMessage(Component.text("다음 땅을 생성하는데 필요한 금액 : ").color(NamedTextColor.YELLOW)
                            .append(Component.text("${requirePrice.toInt()}원").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)))
                        player.sendMessage(Component.text("현재 땅 갯수 : ").color(NamedTextColor.YELLOW)
                            .append(Component.text("${LandManager.getLandsOwned(player.uniqueId).size}개").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)))
                    }),
                CommandAPICommand("땅원")
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

                                player.sendMessage(Component.text(" - 땅원 목록 -").color(NamedTextColor.WHITE))
                                for(lander in land.landers){
                                    player.sendMessage(Component.text("${Bukkit.getOfflinePlayer(lander).name}").color(NamedTextColor.WHITE))
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
                                if (land.landers.contains(target.uniqueId) || land.owner == target.uniqueId) {
                                    player.sendMessage(Component.text("이미 ${target.name}님은 땅원입니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }
                                land.landers.add(target.uniqueId)
                                player.sendMessage(
                                    Component.text("성공적으로 땅원에 추가하였습니다! ( ${target.name} )").color(NamedTextColor.GREEN)
                                )

                                Bukkit.getPlayer(target.uniqueId)?.sendMessage(
                                    Component.text("당신은 ${player.name}님의 땅에 초대를 받으셨습니다!").color(NamedTextColor.GREEN)
                                )
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
                                if(!land.landers.contains(target.uniqueId)){
                                    player.sendMessage(Component.text("${target.name}님은 땅원이 아닙니다!").color(NamedTextColor.RED))
                                    return@PlayerCommandExecutor
                                }
                                land.landers.remove(target.uniqueId)
                                player.sendMessage(Component.text("성공적으로 땅원에서 삭제하였습니다! ( ${target.name} )").color(NamedTextColor.YELLOW))
                                Bukkit.getPlayer(target.uniqueId)?.sendMessage(
                                    Component.text("당신은 ${player.name}님의 땅에서 추방당하셨습니다!").color(NamedTextColor.RED)
                                )
                            }),
                    )
            )
            .register()
    }
}