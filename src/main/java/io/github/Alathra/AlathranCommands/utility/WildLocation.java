package io.github.Alathra.AlathranCommands.utility;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class WildLocation {
    private static int maxX = TPCfg.get().getInt("Settings.WildTP.MaxX");
    private static int maxZ = TPCfg.get().getInt("Settings.WildTP.MaxZ");
    private static int minX = TPCfg.get().getInt("Settings.WildTP.MinX");
    private static int minZ = TPCfg.get().getInt("Settings.WildTP.MinZ");
    private static List<String> blockedBiomes = TPCfg.get().getStringList("Settings.WildTP.Blocked-biomes");
    private static List<String> blockedBlocks = TPCfg.get().getStringList("Settings.WildTP.Blocked-blocks");
    private static Location randomLocation;

    private void getRandomLocation(Player sender) {

    }

    public static CompletableFuture<Location> search(Player player) {
        int x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
        int z = ThreadLocalRandom.current().nextInt(minZ, maxZ + 1);
        Location location = new Location(player.getWorld(), x, 0, z);

        return PaperLib.getChunkAtAsync(location, true).thenApply( chunk -> {
            randomLocation = checkLocation(location);
            if (randomLocation != null)
                return randomLocation;
            return null;
        });
    }

    private static Location checkLocation(Location location) {
        int retries = TPCfg.get().getInt("Settings.WildTP.Retries");

        while (retries > 0) {
            // Checks if biome is banned
//            if (location.getBlock().getBiome() != null && blockedBiomes.contains(location.getBlock().getBiome().toString()))
//                return null;

            // Finds highest Y block
            location.setY(location.getWorld().getHighestBlockYAt(location));

            // Checks if block is allowed
            Block block = location.getBlock();
            Biome biome = location.getBlock().getBiome();
            if (location != null && !blockedBiomes.contains(biome.toString()) && !blockedBlocks.contains(block.getType().name()))
            {
                return location.add(0, 1, 0);
            }
            Bukkit.getLogger().info("Amount of retries left: " + retries);
            retries--;
        }

        if (retries == 0)
            location = null;
        return location;
    }
}
