package com.danielcordell.minequest.questing.intent.intents;

import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IntentStartQuest extends Intent {
    private final int newQuestID;

    public IntentStartQuest(Quest quest, int newQuestID) {
        super(quest);
        this.newQuestID = newQuestID;
    }

    public IntentStartQuest(NBTTagCompound nbt, Quest quest) {
        this(quest, nbt.getInteger("newQuestID"));
    }

    @Override
    public void perform(World world) {
        EntityPlayer player = world.getPlayerEntityByUUID(quest.getPlayerID());
        PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        WorldQuestData wqd = WorldQuestData.get(world);
        Quest newQuest = wqd.getQuestByID(newQuestID);

        if (newQuest != null) {
            pqd.addQuest(newQuest);
        }
    }

    @Override
    public NBTTagCompound toIntentSpecificNBT(NBTTagCompound nbt) {
        nbt.setInteger("newQuestID", newQuestID);
        return nbt;
    }

    @Override
    public IntentType getIntentType() {
        return IntentType.START_QUEST;
    }
}
