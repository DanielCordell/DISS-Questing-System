package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.intent.intents.IntentGiveItemStack;
import com.danielcordell.minequest.questing.intent.intents.IntentSpawnEntity;
import com.danielcordell.minequest.questing.intent.params.PlayerRadiusPosParam;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.*;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.List;
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
        int randVal = rand.nextInt(max+1);
        ObjectiveType objectiveType = null;
        int count = 0;
        //TODO TEST THIS
        MineQuest.logger.info("randVal: " + randVal);
        MineQuest.logger.info("New Weights:");
        for (Map.Entry<ObjectiveType, Integer> entry : objectiveWeights.entrySet()) {
            count += entry.getValue();
            objectiveType = entry.getKey();
            MineQuest.logger.info(entry.getKey().name() + ": " + entry.getValue());
            if (count > randVal) break;
        }
        if (objectiveType == null) {
            MineQuest.logger.error("Quest could not be generated! Starting 'objectiveType' is null!");
            return null;
        }


        ObjectiveBase objective = makeObjectiveFromWorldState(objectiveType, worldState, firstCheckpoint);
        firstCheckpoint.addObjective(objective);
        quest.addCheckpoint(firstCheckpoint);
        quest.addFinishIntent(new IntentGiveItemStack(quest, Util.getRewardFromDifficulty(rand, worldState.overallDifficulty)));

        quest.setQuestName(Util.generateQuestName(quest));

        return quest;
    }

    private static ObjectiveBase makeObjectiveFromWorldState(ObjectiveType objectiveType, WorldState worldState, QuestCheckpoint firstCheckpoint) {
        Quest quest = firstCheckpoint.getQuest();
        ObjectiveParamsBase params;
        if (objectiveType == ObjectiveType.KILL_TYPE) {
            if (worldState.inOrNextToSlimeChunk & rand.nextInt(8) == 0)
                params = new ParamsKillType(firstCheckpoint, "You're near a Slime Chunk, kill some slimes!").setParamDetails(EntitySlime.class, worldState.overallDifficulty);
            else if (!worldState.nearbySpawners.isEmpty()){
                BlockPos pos = worldState.nearbySpawners.get(rand.nextInt(worldState.nearbySpawners.size()));
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) worldState.world.getTileEntity(pos);
                Class<? extends EntityLivingBase> spawnerEntity = spawner.getSpawnerBaseLogic().getSpawnerEntity().getClass().asSubclass(EntityLivingBase.class);
                params = new ParamsKillType(firstCheckpoint, "You're near a Spawner, kill some " + spawner.getSpawnerBaseLogic().getSpawnerEntity().getName() + "s!").setParamDetails(spawnerEntity, worldState.overallDifficulty / 2 + 5);
            }
            else {
                int numToKill = (rand.nextInt(3) + 1) * worldState.overallDifficulty / 2;
                numToKill = numToKill > 0 ? numToKill : 1;
                params = new ParamsKillType(firstCheckpoint, "Kill some enemies!").setParamDetails(
                        Util.getRandomEnemyFromDimension(rand, worldState.dimension), numToKill
                );
            }
        } else if (objectiveType == ObjectiveType.KILL_SPECIFIC) {
            Class<? extends EntityLivingBase> entType = Util.getRandomEnemyFromDimension(rand, worldState.dimension);
            int numToKill = (worldState.overallDifficulty / 5) * (rand.nextInt(5)+2);
            numToKill = numToKill > 0 ? numToKill : 1;
            String nbt = quest.getName()+quest.getQuestID();
            firstCheckpoint.addIntent(new IntentSpawnEntity(quest, entType, (int) (numToKill * 1.5), new PlayerRadiusPosParam(10), nbt, "Ambush"));
            params = new ParamsKillSpecific(firstCheckpoint, "Oh no, you're being attacked!").setParamDetails(nbt, numToKill);
        } else if (objectiveType == ObjectiveType.TRIGGER) {
            throw new NotImplementedException("Not implemented this objective yet");
            //params = new ParamsKillSpecific(firstCheckpoint, "Trigger something!");
        } else if (objectiveType == ObjectiveType.ESCORT) {
            params = new ParamsEscort(firstCheckpoint, "Escort the NPC!");
            List<String> structures = Util.getStructuresFromDimension(worldState.dimension);
            String structure = null;
            while (structure == null || worldState.closestStructurePerType.get(structure).second()) {
                structure = structures.get(rand.nextInt(structures.size()));
            }
            //?Todo pick an NPC in the world, if there is an NPC NOT currently in an open quest and not too far away (farther than closest village) then use them, otherwise make a new one at the closest village.
            EntityNPC npc = new EntityNPC(worldState.world);
            BlockPos villagePos = worldState.closestStructurePerType.get("Village").first();
            villagePos = worldState.world.getTopSolidOrLiquidBlock(villagePos);
            npc.setPosition(villagePos.getX(), villagePos.getY(), villagePos.getZ());
            worldState.world.spawnEntity(npc);
            int entID = quest.addEntity(npc.getUniqueID());

            ((ParamsEscort) params).setParamDetails(entID, (WorldServer) worldState.world, structure, villagePos);
        } else if (objectiveType == ObjectiveType.GATHER) {
            ItemStack itemStack = Util.getGatherFromDimAndDifficulty(rand, worldState.dimension, worldState.overallDifficulty);
            params = new ParamsGather(firstCheckpoint, "Gather some Resources!").setParamDetails(itemStack, itemStack.getCount());
        } else if (objectiveType == ObjectiveType.SEARCH) {
            List<String> structures = Util.getStructuresFromDimension(worldState.dimension);
            String structure = null;
            while (structure == null || worldState.closestStructurePerType.get(structure).second()) {
                structure = structures.get(rand.nextInt(structures.size()));
            }
            params = new ParamsSearch(firstCheckpoint, "Search for a " + structure + "!").setParamDetails(structure);
        } else if (objectiveType == ObjectiveType.DELIVER) {
            ItemStack itemStack = Util.getGatherFromDimAndDifficulty(rand, worldState.dimension, worldState.overallDifficulty);
            // Todo delive ronly takes exact amounts that is WRONG
            //?Todo pick an NPC, if there is an NPC NOT currently in an open quest then use them, otherwise make a new one at the closest village.
            params = new ParamsDeliver(firstCheckpoint, "Deliver to an NPC!");

            EntityNPC npc = new EntityNPC(worldState.world);
            BlockPos villagePos = worldState.closestStructurePerType.get("Village").first();
            villagePos = worldState.world.getTopSolidOrLiquidBlock(villagePos);
            npc.setPosition(villagePos.getX(), villagePos.getY(), villagePos.getZ());
            worldState.world.spawnEntity(npc);
            int entID = quest.addEntity(npc.getUniqueID());
            ((ParamsDeliver) params).setParamDetails(itemStack, itemStack.getCount(), entID, (WorldServer) worldState.world, npc.getPosition());
        }
        else {
            MineQuest.logger.error("Could not construct an objective, bad ObjectiveType");
            return null;
        }
        return ObjectiveBuilder.fromParams(params, objectiveType);
    }
}