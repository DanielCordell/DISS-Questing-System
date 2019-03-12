package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.ParamsKillSpecific;
import com.danielcordell.minequest.questing.objective.params.ParamsKillType;
import com.danielcordell.minequest.questing.objective.params.ParamsSearch;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuestGeneratorPreviousCheckpoint {
    private static Random rand = new Random(System.currentTimeMillis());

    public static Quest generate(WorldServer world, EntityPlayerMP player) {
        Quest quest = Quest.newEmptyQuest(world);
        QuestCheckpoint firstCheckpoint = new QuestCheckpoint(quest);
        WorldState worldState = WorldState.getWorldState(world, player);
        HashMap<ObjectiveType, Integer> objectiveWeights = ObjectiveType.getObjectiveWeightMap(worldState);

        //Determine Objective for first Checkpoint
        int max = objectiveWeights.values().stream().mapToInt(Integer::intValue).sum();
        int randVal = world.rand.nextInt(max+1);
        ObjectiveType objectiveType = null;
        int count = 0;
        for (Map.Entry<ObjectiveType, Integer> entry : objectiveWeights.entrySet()) {
            count += entry.getValue();
            objectiveType = entry.getKey();
            if (count > randVal) break;
        }
        if (objectiveType == null) {
            MineQuest.logger.error("Quest could not be generated! Starting 'objectiveType' is null!");
            return null;
        }


        ObjectiveBase objective = makeObjectiveFromWorldState(objectiveType, worldState, firstCheckpoint);
        firstCheckpoint.addObjective(objective);

        return quest;
    }

    private static ObjectiveBase makeObjectiveFromWorldState(ObjectiveType objectiveType, WorldState worldState, QuestCheckpoint firstCheckpoint) {
        ObjectiveParamsBase params;
        if (objectiveType == ObjectiveType.KILL_TYPE) {
            if (worldState.inOrNextToSlimeChunk & rand.nextInt(8) == 0)
                params = new ParamsKillType(firstCheckpoint, "You're near a Slime Chunk, kill some slimes!").setParamDetails(EntitySlime.class, worldState.overallDifficulty);
            else if (!worldState.nearbySpawners.isEmpty()){
                BlockPos pos = worldState.nearbySpawners.get(rand.nextInt(worldState.nearbySpawners.size()));
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) worldState.world.getTileEntity(pos);
                Class<? extends EntityLivingBase> spawnerEntity = spawner.getSpawnerBaseLogic().getSpawnerEntity().getClass().asSubclass(EntityLivingBase.class);
                params = new ParamsKillType(firstCheckpoint, "You're near a Spawner, kill some " + spawner.getSpawnerBaseLogic().getSpawnerEntity().getName() + "s!").setParamDetails(spawnerEntity, worldState.overallDifficulty);
            }
            else {
                params = new ParamsKillType(firstCheckpoint, "Kill some enemies!").setParamDetails(
                        Util.getRandomEnemyFromDimension(rand, worldState.dimension), (rand.nextInt(3) + 1) * worldState.overallDifficulty / 2
                );
            }
        } else if (objectiveType == ObjectiveType.KILL_SPECIFIC) {
            params = new ParamsKillSpecific(firstCheckpoint, "Kill these enemies!");
        } else if (objectiveType == ObjectiveType.TRIGGER) {
            params = new ParamsKillSpecific(firstCheckpoint, "Trigger something!");
        } else if (objectiveType == ObjectiveType.ESCORT) {
            params = new ParamsKillType(firstCheckpoint, "Escort the NPC!");
        } else if (objectiveType == ObjectiveType.GATHER) {
            params = new ParamsKillSpecific(firstCheckpoint, "Gather some Resources!");
        } else if (objectiveType == ObjectiveType.SEARCH) {
            params = new ParamsKillSpecific(firstCheckpoint, "Search for a structure!");
        } else if (objectiveType == ObjectiveType.DELIVER) {
            params = new ParamsKillSpecific(firstCheckpoint, "Deliver to an NPC!");
        }
        else {
            MineQuest.logger.error("Could not construct an objective, bad ObjectiveType");
            return null;
        }
        return ObjectiveBuilder.fromParams(params, objectiveType);
    }
}
