package io.github.Alathra.AlathranCommands.utility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Coord;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
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
    private static int townDistance = TPCfg.get().getInt("Settings.WildTP.Town-distance");

    private static int randomX() {
        return ThreadLocalRandom.current().nextInt(minX, maxX + 1);
    }

    private static int randomZ() {
        return ThreadLocalRandom.current().nextInt(minZ, maxZ + 1);
    }

    public static CompletableFuture<Location> search(Player player) {
        int x = randomX();
        int z = randomZ();
        Location location = new Location(player.getWorld(), x, 0, z);

        return PaperLib.getChunkAtAsync(location, true).thenApply( chunk -> {
            int retries = TPCfg.get().getInt("Settings.WildTP.Retries");
            while (retries > 0) {
                @Nullable Location randomLocation = checkLocation(location);
                if (randomLocation != null)
                    return randomLocation;

                location.set(randomX(), 0, randomZ());
                
                retries--;
            }
            return null;
        });
    }

    @Nullable private static Location checkLocation(Location location) {
        // Finds highest Y block
        location.setY(location.getWorld().getHighestBlockYAt(location));

        // Checks if block and biome is allowed
        Block block = location.getBlock();
        Biome biome = location.getBlock().getBiome();
        if (!blockedBiomes.contains(biome.toString()) && !blockedBlocks.contains(block.getType().name()))
        {
            if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
                if (TownyAPI.getInstance().getTownyWorld(location.getWorld()).getMinDistanceFromOtherTownsPlots(Coord.parseCoord(location)) < townDistance) {
                    return location.add(0, 1, 0);
                }
                return null;
            }
            return location.add(0, 1, 0);
        }
        return null;
    }
}
