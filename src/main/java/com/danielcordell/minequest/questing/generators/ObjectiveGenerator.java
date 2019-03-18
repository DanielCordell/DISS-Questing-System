package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.intent.intents.IntentSpawnEntity;
import com.danielcordell.minequest.questing.intent.params.PlayerRadiusPosParam;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.*;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
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
        int numToKill = (worldState.overallDifficulty / 4) * (rand.nextInt(5)+4);
        numToKill = numToKill > 0 ? numToKill : 1;
        String nbt = quest.getName()+quest.getQuestID();
        firstCheckpoint.addIntent(new IntentSpawnEntity(quest, entType, numToKill, new PlayerRadiusPosParam(10), nbt, "Ambush",  worldState.overallDifficulty / 5));
        return new ParamsKillSpecific(firstCheckpoint, "Oh no, you're being attacked!").setParamDetails(nbt, numToKill);
    }

    static ParamsEscort generateEscortObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, String structure) {
        ParamsEscort params = new ParamsEscort(firstCheckpoint, "Escort the NPC!");
        EntityNPC npc = new EntityNPC(worldState.world);
        npc.changeDimension(worldState.dimension);
        BlockPos spawnPos;
        if (worldState.dimension == DimensionType.OVERWORLD.getId()) {
            spawnPos = worldState.closestStructurePerType.get("Village").first();
            spawnPos = worldState.world.getTopSolidOrLiquidBlock(spawnPos).add(0, 1, 0);
        } else {
            spawnPos = worldState.playerPos.add(0, 0.5, 0);
        }
        npc.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        worldState.world.spawnEntity(npc);
        int entID = firstCheckpoint.getQuest().addEntity(npc.getUniqueID());

        return params.setParamDetails(entID, (WorldServer) worldState.world, structure, spawnPos);
    }

    static ParamsGather generateGatherObjective(QuestCheckpoint firstCheckpoint, ItemStack itemStack) {
        return new ParamsGather(firstCheckpoint, "Gather some Resources!").setParamDetails(itemStack, itemStack.getCount());
    }

    static ParamsSearch generateSearchObjective(QuestCheckpoint firstCheckpoint, String structure) {
        return new ParamsSearch(firstCheckpoint, "Search for a " + structure + "!").setParamDetails(structure);
    }

    static ObjectiveParamsBase generateDeliverObjective(QuestCheckpoint firstCheckpoint, WorldState worldState, ItemStack itemStack) {
        EntityNPC npc = new EntityNPC(worldState.world);
        BlockPos villagePos = worldState.closestStructurePerType.get("Village").first();
        villagePos = worldState.world.getTopSolidOrLiquidBlock(villagePos);
        npc.setPosition(villagePos.getX(), villagePos.getY(), villagePos.getZ());
        worldState.world.spawnEntity(npc);
        int entID = firstCheckpoint.getQuest().addEntity(npc.getUniqueID());
        return new ParamsDeliver(firstCheckpoint, "Deliver some resources to an NPC!").setParamDetails(itemStack, itemStack.getCount(), entID, npc.getPosition());
    }
}
