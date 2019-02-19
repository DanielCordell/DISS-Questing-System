package com.danielcordell.minequest.questing.quest;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

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
    //For specific KNOWN entities at the start of the quest. Must exist throughout the entire quest, not just for an objective/checkpoint.
    private HashMap<Integer, Integer> entityMap;

    private int currentCheckpontIndex;
    private ArrayList<QuestCheckpoint> checkpoints;

    //Should be synced to client.
    private boolean isDirty;

    private int currentEntityIDCounter;

    Quest(int questID, String questName, UUID playerID, QuestState state, int currentCheckpontIndex, HashMap<Integer, Integer> entityMap) {
        this.questID = questID;
        this.questName = questName;
        this.playerID = playerID;
        this.currentCheckpontIndex = currentCheckpontIndex;
        this.state = state;
        this.entityMap = entityMap;
        this.currentEntityIDCounter = entityMap.isEmpty() ? 0 : Collections.max(entityMap.keySet()) + 1;
        this.checkpoints = new ArrayList<>();
        setDirty();
    }

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

    public Quest addEntity(EntityLiving entity) {
        entityMap.put(currentEntityIDCounter++, entity.getEntityId());
        setDirty();
        return this;
    }

    public int getQuestID() {
        return questID;
    }
    public String getName() {
        return questName;
    }
    public QuestState getState() {
        return state;
    }

    void addCheckpoint(QuestCheckpoint checkpoint) {
        this.checkpoints.add(checkpoint);
        setDirty();
    }

    public void setPlayer(EntityPlayer player) {
        playerID = player.getUniqueID();
        setDirty();
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty() {
        isDirty = true;
    }

    public void setClean() {
        isDirty = false;
    }

    public void start() {
        if (state != QuestState.CREATED) {
            MineQuest.logger.error("Trying to start an already started quest, whoops!");
            MineQuest.logger.error("Quest: " + toNBT());
            return;
        }
        state = QuestState.STARTED;
    }

    public ArrayList<ObjectiveBase> getCurrentCheckpointObjectives() {
        return new ArrayList<>(checkpoints.get(currentCheckpontIndex).getObjectives());
    }

    public void update(World world) {
        if (state == QuestState.COMPLETED || state == QuestState.FAILED) return;
        //Check current checkpoint for completion.
        if (getCurrentCheckpointObjectives().stream().allMatch(chkpnt -> chkpnt.getState() == QuestState.COMPLETED)) {
            if (progressCheckpoint()) completeQuest(world);
            setDirty();
        }
    }

    private void completeQuest(World world) {
        world.getPlayerEntityByUUID(playerID).sendMessage(new TextComponentString("Quest Complete: " + questName));
        state = QuestState.COMPLETED;
    }

    // Returns true if the quest has just been completed.
    private boolean progressCheckpoint() {
        if (currentCheckpontIndex+1 == checkpoints.size()) {
            MineQuest.logger.info("Quest complete!");
            return true;
        }
        else currentCheckpontIndex++;
        return false;
    }

    public UUID getPlayerID() {
        return playerID;
    }
}
