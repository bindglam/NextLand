package io.github.bindglam.nextland;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Land implements ConfigurationSerializable {
    private final UUID owner;
    private final @Nullable OfflinePlayer ownerPlayer;
    private final ChunkPos chunkPos;

    private final List<UUID> admins = new ArrayList<>();
    private String name;

    public Land(UUID owner, ChunkPos chunkPos) {
        this.owner = owner;
        this.ownerPlayer = Bukkit.getOfflinePlayer(owner);
        this.chunkPos = chunkPos;
        this.name = ownerPlayer.getName() != null ? ownerPlayer.getName() + "님의 땅" : "누군가의 땅";
    }

    public Land(Map<String, Object> map) {
        this(UUID.fromString((String) map.get("owner")), (ChunkPos) map.get("pos"));
        this.name = (String) map.get("name");
        this.admins.addAll(((List<String>) map.get("admins")).stream().map(UUID::fromString).toList());
    }

    public UUID getOwner() {
        return owner;
    }

    public @Nullable OfflinePlayer getOwnerPlayer() {
        return ownerPlayer;
    }

    public ChunkPos getPos() {
        return chunkPos;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("owner", owner.toString(),
                "pos", chunkPos,
                "name", name,
                "admins", admins.stream().map(UUID::toString).toList());
    }
}
