package io.github.bindglam.nextland.events;

import io.github.bindglam.nextland.utils.ChunkPos;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class LandCreateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ChunkPos pos;

    public LandCreateEvent(@NotNull Player who, ChunkPos pos) {
        super(who);
        this.pos = pos;
    }

    public ChunkPos getPos() {
        return pos;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
