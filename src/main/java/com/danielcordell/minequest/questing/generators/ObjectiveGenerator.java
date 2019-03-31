package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.intent.intents.IntentSetNPCFollow;
import com.danielcordell.minequest.questing.intent.intents.IntentSpawnEntity;
import com.danielcordell.minequest.questing.intent.params.PlayerRadiusPosParam;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.*;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;

import java.util.Random;

public class ObjectiveGenerator {
    static Random rand = new Random(System.currentTimeMillis());

    static ObjectiveParamsBase generateKillSpecificObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, Class<? extends EntityLivingBase> entType) {
        Quest quest = firstCheckpoint.getQuest();
        int numToKill = (worldState.overallDifficulty / 3) + 1;
        numToKill = numToKill > 0 ? numToKill : 1;
        String nbt = quest.getName()+quest.getQuestID();
        firstCheckpoint.addIntent(new IntentSpawnEntity(quest, entType, numToKill, new PlayerRadiusPosParam(10), nbt, "Ambush",  worldState.overallDifficulty / 5));
        return new ParamsKillSpecific(firstCheckpoint, "Oh no, you're being attacked!").setParamDetails(nbt, numToKill);
    }

    private static ParamsEscort generateEscortObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, String structure, int questEntityID, BlockPos pos) {
        return new ParamsEscort(firstCheckpoint, "Escort the NPC!")
                .setParamDetails(questEntityID, (WorldServer) worldState.world, structure, pos);
    }

    static ParamsEscort generateEscortObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, String structure, int questEntityID) {
        firstCheckpoint.addIntent(new IntentSetNPCFollow(firstCheckpoint.getQuest(), questEntityID));
        return generateEscortObjective(firstCheckpoint, worldState, structure, questEntityID, Util.getNPCFromQuestIDOrNull(questEntityID, worldState.world, firstCheckpoint.getQuest()).getPosition());
    }

    static ParamsEscort generateEscortObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, String structure) {
        BlockPos spawnPos;
        if (worldState.dimension == DimensionType.OVERWORLD.getId()) {
            spawnPos = worldState.closestStructurePerType.get("Village").first();
            spawnPos = worldState.world.getTopSolidOrLiquidBlock(spawnPos).add(0, 1, 0);
        } else {
            spawnPos = worldState.playerPos.add(0, 0.5, 0);
        }
        EntityNPC npc = new EntityNPC(worldState.world);
        npc.dimension = worldState.dimension;
        npc.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        worldState.world.spawnEntity(npc);
        int entID = firstCheckpoint.getQuest().addEntity(npc.getUniqueID());
        firstCheckpoint.addIntent(new IntentSetNPCFollow(firstCheckpoint.getQuest(), entID));
        return generateEscortObjective(firstCheckpoint, worldState, structure, entID, spawnPos);
    }

    static ParamsGather generateGatherObjective(QuestCheckpoint firstCheckpoint, ItemStack itemStack) {
        return new ParamsGather(firstCheckpoint, "Gather some Resources!").setParamDetails(itemStack, itemStack.getCount());
    }

    static ParamsSearch generateSearchObjective(QuestCheckpoint firstCheckpoint, String structure) {
        return new ParamsSearch(firstCheckpoint, "Search for a " + structure + "!").setParamDetails(structure);
    }

    private static ObjectiveParamsBase generateDeliverObjective(QuestCheckpoint firstCheckpoint, ItemStack itemStack, int questEntityID, BlockPos pos) {
        return new ParamsDeliver(firstCheckpoint, "Deliver some resources to an NPC!")
                .setParamDetails(itemStack, itemStack.getCount(), questEntityID, pos);

    }

    static ObjectiveParamsBase generateDeliverObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, ItemStack itemStack, int questEntityID) {
        return generateDeliverObjective(firstCheckpoint, itemStack, questEntityID, Util.getNPCFromQuestIDOrNull(questEntityID, worldState.world, firstCheckpoint.getQuest()).getPosition());
    }

    static ObjectiveParamsBase generateDeliverObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, ItemStack itemStack) {
        BlockPos spawnPos;
        if (worldState.dimension == DimensionType.OVERWORLD.getId()) {
            spawnPos = worldState.closestStructurePerType.get("Village").first();
            spawnPos = worldState.world.getTopSolidOrLiquidBlock(spawnPos).add(0, 1, 0);
        } else {
            spawnPos = worldState.playerPos.add(0, 0.5, 0);
        }
        EntityNPC npc = new EntityNPC(worldState.world);
        npc.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        npc.dimension = worldState.dimension;
        worldState.world.spawnEntity(npc);
        int entID = firstCheckpoint.getQuest().addEntity(npc.getUniqueID());
        return generateDeliverObjective(firstCheckpoint, itemStack, entID, spawnPos);
    }
}
