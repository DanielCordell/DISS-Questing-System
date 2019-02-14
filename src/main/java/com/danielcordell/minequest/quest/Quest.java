package com.danielcordell.minequest.quest;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class Quest {
    // For setting up quest entities for quests that have been loaded from save data, should not be saved!
    private int questID;
    private String questName;
    private UUID playerID;
    private QuestState state;

    //QuestEntityID -> WorldEntityID
    private HashMap<Integer, Integer> entityMap;

    private int currentCheckpontIndex;
    private ArrayList<QuestCheckpoint> checkpoints;

    //Should be synced to client.
    private boolean isDirty;

    private int currentEntityIDCounter;

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("questID", questID);
        nbt.setString("questName", questName);
        if (playerID != null) nbt.setUniqueId("playerID", playerID);
        nbt.setInteger("currentCheckpointIndex", currentCheckpontIndex);
        nbt.setInteger("state", state.stateInt);

        NBTTagCompound entityMapNBT = new NBTTagCompound();
        entityMap.forEach((questEntityID, entity) -> entityMapNBT.setInteger(questEntityID.toString(), entity));
        nbt.setTag("entityMap", entityMapNBT);

        NBTTagList checkpointsNBT = new NBTTagList();
        checkpoints.forEach(checkpoint -> checkpointsNBT.appendTag(checkpoint.toNBT()));
        nbt.setTag("checkpoints", checkpointsNBT);

        return nbt;
    }

    Quest(int questID, String questName, UUID playerID, QuestState state, int currentCheckpontIndex, HashMap<Integer, Integer> entityMap) {
        this.questID = questID;
        this.questName = questName;
        this.playerID = playerID;
        this.currentCheckpontIndex = currentCheckpontIndex;
        this.state = state;
        this.entityMap = entityMap;
        this.currentEntityIDCounter = entityMap.isEmpty() ? 0 : Collections.max(entityMap.keySet()) + 1;
        this.checkpoints = new ArrayList<>();
        isDirty = true;
    }

    public Quest addEntity(EntityLiving entity) {
        entityMap.put(currentEntityIDCounter++, entity.getEntityId());
        isDirty = true;
        return this;
    }

    public int getQuestID() {
        return questID;
    }

    public void addCheckpoint(QuestCheckpoint checkpoint) {
        this.checkpoints.add(checkpoint);
        isDirty = true;
    }

    public void setPlayer(EntityPlayer player) {
        playerID = player.getUniqueID();
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    private void setClean() {
        isDirty = false;
    }
}
