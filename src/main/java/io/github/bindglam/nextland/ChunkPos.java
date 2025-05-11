package io.github.bindglam.nextland;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChunkPos implements ConfigurationSerializable {
    private World world;
    private int x, z;
    private Location location;

    public ChunkPos(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;

        updateLocation();
    }

    public ChunkPos(Map<String, Object> map) {
        this(Bukkit.getWorld((String) map.get("world")), (Integer) map.get("x"), (Integer) map.get("z"));
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;

        updateLocation();
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;

        updateLocation();
    }

    public Location getLocation() {
        return location.clone();
    }

    private void updateLocation() {
        getChunk().thenAccept((chunk) -> location = chunk.getBlock(0, 0, 0).getLocation());
    }

    public CompletableFuture<Chunk> getChunk() {
        return world.getChunkAtAsync(x, z);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("world", world.getName(), "x", x, "z", z);
    }
}
