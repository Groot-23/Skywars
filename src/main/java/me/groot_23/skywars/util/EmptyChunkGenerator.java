package me.groot_23.skywars.util;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class EmptyChunkGenerator extends ChunkGenerator
{
	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
		ChunkData chunk = createChunkData(world);
		for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                for (int y = 0; y <= 10; y++) {
                    chunk.setBlock(X, y, Z, Material.AIR);
                }
            }
        }
        return chunk;
	}
}
