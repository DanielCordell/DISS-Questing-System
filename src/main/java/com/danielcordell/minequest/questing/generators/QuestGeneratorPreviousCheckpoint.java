package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.quest.Quest;
import ibxm.Player;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class QuestGeneratorPreviousCheckpoint {
    public static Quest generate(WorldServer world, EntityPlayerMP player) {
        Quest quest = Quest.newEmptyQuest(world);

        WorldState worldState = WorldState.getWorldState(world, player);
        // Maybe include nearest of every type of structure?
        return quest;
    }
}
