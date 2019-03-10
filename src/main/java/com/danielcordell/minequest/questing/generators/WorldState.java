package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.Util;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WorldState {
    public Biome biome;
    public List<EntityLiving> nearbyMobs;
    public boolean inOrNextToSlimeChunk;

    public List<BlockPos> nearbySpawners;
    public boolean isDaytime;
    public int dimension;

    public long worldTime;
    public int playerAlive;

    public HashMap<String, BlockPos> closestStructurePerType;

    public static WorldState getWorldState(WorldServer world, EntityPlayerMP player) {
        WorldState worldState = new WorldState();
        worldState.isDaytime = world.isDaytime();

        BlockPos pos = player.getPosition();

        Chunk[] chunks = {world.getChunkFromBlockCoords(pos), world.getChunkFromBlockCoords(pos.add(128, 0, 0)),
                world.getChunkFromBlockCoords(pos.add(-128, 0, 0)), world.getChunkFromBlockCoords(pos.add(0, 0, 128)),
                world.getChunkFromBlockCoords(pos.add(0, 0, -128))};


        worldState.biome = world.getBiome(pos);
        worldState.nearbyMobs = world.getEntitiesWithinAABB(EntityLiving.class,
                new AxisAlignedBB(pos.getX() - 128, 0, pos.getZ() - 128, pos.getX() + 128, 255, pos.getZ() + 128),
                entity -> entity instanceof IMob
        );
        worldState.inOrNextToSlimeChunk = Arrays.stream(chunks).anyMatch(chunk -> chunk.getRandomWithSeed(987234911L).nextInt(10) == 0);

        worldState.nearbySpawners = new ArrayList<>();
        for (Chunk chunk : chunks) {
            chunk.getTileEntityMap().forEach((key, value) -> {
                if (value instanceof TileEntityMobSpawner) worldState.nearbySpawners.add(key);
            });
        }

        worldState.dimension = player.dimension;
        worldState.closestStructurePerType = new HashMap<>();
        Util.getStructuresFromDimension(worldState.dimension).forEach(type -> worldState.closestStructurePerType
                .put(type, world.getChunkProvider().getNearestStructurePos(world, type, player.getPosition(), true))
        );

        //Difficulty
        worldState.worldTime = world.getTotalWorldTime();
        worldState.playerAlive = player.ticksExisted;
        return worldState;
    }
}
