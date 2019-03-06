package com.danielcordell.minequest.questing.quest;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.*;

public class Quest {
    // For setting up quest entities for quests that have been loaded from save data, should not be saved!
    private int questID;
    private String questName;
    private UUID playerID;
    private QuestState state;

    //QuestEntityID -> WorldEntityID
    //For specific KNOWN entities at the start of the quest. Must exist throughout the entire quest, not just for an objective/checkpoint.
    private HashMap<Integer, UUID> entityMap;

    private int currentCheckpointIndex;
    private ArrayList<QuestCheckpoint> checkpoints;

    //Should be synced to client.
    private boolean isDirty;

    private int currentEntityIDCounter;

    private ArrayList<Intent> onFinishIntents;

    Quest(int questID, String questName, UUID playerID, QuestState state, int currentCheckpointIndex, HashMap<Integer, UUID> entityMap) {
        this.questID = questID;
        this.questName = questName;
        this.playerID = playerID;
        this.currentCheckpointIndex = currentCheckpointIndex;
        this.state = state;
        this.entityMap = entityMap;
        this.currentEntityIDCounter = entityMap.isEmpty() ? 0 : Collections.max(entityMap.keySet()) + 1;
        this.checkpoints = new ArrayList<>();
        this.onFinishIntents = new ArrayList<>();
        setDirty();
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("questID", questID);
        nbt.setString("questName", questName);
        if (playerID != null) nbt.setUniqueId("playerID", playerID);
        nbt.setInteger("currentCheckpointIndex", currentCheckpointIndex);
        nbt.setInteger("state", state.stateInt);

        NBTTagCompound entityMapNBT = new NBTTagCompound();
        entityMap.forEach((questEntityID, entity) -> entityMapNBT.setUniqueId(questEntityID.toString(), entity));
        nbt.setTag("entityMap", entityMapNBT);

        NBTTagList checkpointsNBT = new NBTTagList();
        checkpoints.forEach(checkpoint -> checkpointsNBT.appendTag(checkpoint.toNBT()));
        nbt.setTag("checkpoints", checkpointsNBT);

        NBTTagList intentsNBT = new NBTTagList();
        onFinishIntents.forEach(intent -> intentsNBT.appendTag(intent.toNBT()));
        nbt.setTag("onFinishIntents", intentsNBT);
        return nbt;
    }

    // Returns the internal ID of the entity just created.
    // If that entity already exists in the list, return the existing ID
    public int addEntity(UUID entityID) {
        if (entityMap.containsValue(entityID)){
            Optional<Map.Entry<Integer, UUID>> foundID = entityMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == entityID)
                    .findFirst();
            if (foundID.isPresent()) return foundID.get().getKey();
        }

        int questEntityID = currentEntityIDCounter++;
        entityMap.put(questEntityID, entityID);
        setDirty();
        return questEntityID;
    }

    // Using the Entity ID,
    public int getQuestEntityIDFromEntityID(UUID entityID) {
        Optional<Map.Entry<Integer, UUID>> found = entityMap.entrySet()
                .stream()
                .filter(pair -> pair.getValue().compareTo(entityID) == 0)
                .findFirst();
        if (found.isPresent()) return found.get().getKey();
        else return -1;
    }

    // Using
    public UUID getEntityIDFromQuestEntityID(int questEntityID) {
        Optional<Map.Entry<Integer, UUID>> found = entityMap.entrySet()
                .stream()
                .filter(pair -> pair.getKey() == questEntityID)
                .findFirst();
        if (found.isPresent()) return found.get().getValue();
        else return Util.emptyUUID;
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

    public void addCheckpoint(QuestCheckpoint checkpoint) {
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

    public void start(World world) {
        if (state != QuestState.CREATED) {
            MineQuest.logger.error("Trying to start an already started quest, whoops!");
            MineQuest.logger.error("Quest: " + toNBT());
            return;
        }
        state = QuestState.STARTED;
        performCurrentCheckpointIntents(world);
        setDirty();
    }

    public void performCurrentCheckpointIntents(World world) {
        checkpoints.get(currentCheckpointIndex).performIntents(world);
        setDirty();
    }

    public ArrayList<ObjectiveBase> getCurrentCheckpointObjectives() {
        return new ArrayList<>(checkpoints.get(currentCheckpointIndex).getObjectives());
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
        EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(getPlayerID());
        state = QuestState.COMPLETED;

        if (player != null) {
            SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("Quest Complete").setStyle(new Style().setColor(TextFormatting.WHITE)));
            player.connection.sendPacket(packet);
            packet = new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(getName()).setStyle(new Style().setColor(TextFormatting.GRAY).setItalic(true)));
            player.connection.sendPacket(packet);
        }
        onFinishIntents.forEach(intent -> intent.perform(world));
        setDirty();
    }

    // Returns true if the quest has just been completed.
    private boolean progressCheckpoint() {
        if (currentCheckpointIndex +1 == checkpoints.size()) {
            MineQuest.logger.info("Quest complete!");
            return true;
        }
        else currentCheckpointIndex++;
        setDirty();
        return false;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public void addFinishIntent(Intent intent) {
        onFinishIntents.add(intent);
        setDirty();
    }
}
