package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.Conf;
import com.danielcordell.minequest.Util;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldState {
    public Biome biome;
    public List<EntityLiving> nearbyMobs;
    public boolean inOrNextToSlimeChunk;

    public List<BlockPos> nearbySpawners;
    public int dimension;

    public long worldTime;
    public int playerAlive;

    public BlockPos playerPos;

    public HashMap<String, Pair<BlockPos, Boolean>> closestStructurePerType;
    public boolean inWater;
    public int overallDifficulty;

    public World world;
    public boolean isNight;
    public UUID playerID;

    public static WorldState getWorldState(WorldServer world, EntityPlayerMP player) {
        WorldState worldState = new WorldState();
        worldState.world = world;
        BlockPos pos = player.getPosition();

        worldState.playerID = player.getUniqueID();
        worldState.playerPos = player.getPosition();
        worldState.isNight = !world.isDaytime();

        Chunk[] chunks = {world.getChunkFromBlockCoords(pos), world.getChunkFromBlockCoords(pos.add(128, 0, 0)),
                world.getChunkFromBlockCoords(pos.add(-128, 0, 0)), world.getChunkFromBlockCoords(pos.add(0, 0, 128)),
                world.getChunkFromBlockCoords(pos.add(0, 0, -128))};


        worldState.biome = world.getBiome(pos);
        worldState.nearbyMobs = world.getEntitiesWithinAABB(EntityLiving.class,
                new AxisAlignedBB(pos.getX() - 128, player.posY - 20, pos.getZ() - 128, pos.getX() + 128, player.posY + 20, pos.getZ() + 128),
                entity -> entity instanceof IMob
        );
        worldState.inOrNextToSlimeChunk = Arrays.stream(chunks).anyMatch(chunk -> chunk.getRandomWithSeed(987234911L).nextInt(10) == 0);

        worldState.nearbySpawners = new ArrayList<>();
        for (Chunk chunk : chunks)
            chunk.getTileEntityMap().forEach((key, value) -> {
                if (value instanceof TileEntityMobSpawner && worldState.playerPos.getDistance(key.getX(), key.getY(), key.getZ()) < 32) worldState.nearbySpawners.add(key);
            });

        worldState.dimension = player.dimension;
        worldState.closestStructurePerType = new HashMap<>();
        Util.getStructuresFromDimension(worldState.dimension).forEach(type -> worldState.closestStructurePerType
                .put(type, Pair.of(
                        world.getChunkProvider().getNearestStructurePos(world, type, player.getPosition(), true),
                        world.getChunkProvider().isInsideStructure(world, type, player.getPosition())
                ))
        );

        worldState.inWater = player.isInWater();

        //Difficulty
        worldState.worldTime = world.getTotalWorldTime();
        worldState.playerAlive = player.ticksExisted;

        worldState.overallDifficulty = (int) Math.round(Math.min(
                Math.pow(worldState.worldTime / 24000.f <= 10 ? worldState.playerAlive : worldState.worldTime, 0.25f) / 4,
                20
        ));

        if (Conf.shouldDifficultyScaleFaster) worldState.overallDifficulty = Math.min(worldState.overallDifficulty * 5, 20);

        return worldState;
    }
}
