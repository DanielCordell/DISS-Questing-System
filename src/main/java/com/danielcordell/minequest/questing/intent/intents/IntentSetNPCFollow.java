package com.danielcordell.minequest.questing.intent.intents;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

public class IntentSetNPCFollow extends Intent {

    private int npcID;

    public IntentSetNPCFollow(Quest quest, int npcID) {
        super(quest);
        this.npcID = npcID;
    }

    public IntentSetNPCFollow(NBTTagCompound nbt, Quest quest) {
        this(quest, nbt.getInteger("npcID"));
    }

    @Override
    public void perform(World world) {
        Entity entity = world.getEntityByID(quest.getEntityIDFromQuestEntityID(npcID));
        if (!(entity instanceof EntityNPC)) {
            MineQuest.logger.error("IntentSetNPCFollow has a bad NPC!");
            return;
        }
        EntityNPC npc = ((EntityNPC) entity);
        EntityPlayer player = world.getPlayerEntityByUUID(quest.getPlayerID());
        if (player == null) {
            MineQuest.logger.error("IntentSetNPCFollow has a bad Player!");
            return;
        }
        npc.setNPCFollow(player.getUniqueID());
    }

    @Override
    public NBTTagCompound toIntentSpecificNBT(NBTTagCompound nbt) {
        nbt.setInteger("npcID", npcID);
        return nbt;
    }

    @Override
    public IntentType getIntentType() {
        return IntentType.SET_NPC_FOLLOW;
    }
}
