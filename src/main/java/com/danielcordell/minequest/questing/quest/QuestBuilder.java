package com.danielcordell.minequest.questing.quest;

import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.intent.IntentBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QuestBuilder {
    private int questID;
    private String questName;
    private UUID playerID;
    private QuestState state = QuestState.CREATED;
    private int currentCheckpontIndex = 0;
    private ConcurrentHashMap<Integer, UUID> entityMap = new ConcurrentHashMap<>();

    public static Quest fromNBT(NBTTagCompound nbt) {
        UUID id = nbt.getUniqueId("playerID");
        if (Util.isEmptyUUID(id)) id = null;
        QuestBuilder qb = new QuestBuilder(nbt.getInteger("questID"), nbt.getString("questName"), id);
        qb.setCurrentCheckpontIndex(nbt.getInteger("currentCheckpointIndex"))
                .setState(QuestState.getStateFromInt(nbt.getInteger("state")));

        NBTTagCompound entityMapNBT = nbt.getCompoundTag("entityMap");
        if (entityMapNBT.getSize() != 0) {
            int count = 0;
            while (true) {
                UUID uuid = entityMapNBT.getUniqueId(String.valueOf(count));
                if (Util.isEmptyUUID(uuid)) break;
                qb.entityMap.put(count++, uuid);
            }
        }

        Quest newQuest = qb.build();

        NBTTagList checkpointsNBT = ((NBTTagList) nbt.getTag("checkpoints"));
        if (checkpointsNBT.tagCount() != 0) checkpointsNBT.forEach(checkpoint ->
                newQuest.addCheckpoint(new QuestCheckpoint(newQuest, (NBTTagCompound) checkpoint))
        );

        NBTTagList finishIntentsNBT = ((NBTTagList) nbt.getTag("onFinishIntents"));
        if (finishIntentsNBT.tagCount() != 0) finishIntentsNBT.forEach(intent ->
                newQuest.addFinishIntent(IntentBuilder.fromNBT(newQuest, (NBTTagCompound) intent))
        );

        return newQuest;
    }

    public QuestBuilder(int questID, String questName, UUID playerID) {
        this.questID = questID;
        this.questName = questName;
        this.playerID = playerID;
    }

    public QuestBuilder(int questID, String questName) {
        this(questID, questName, null);
    }

    public QuestBuilder setCurrentCheckpontIndex(int currentCheckpontIndex) {
        this.currentCheckpontIndex = currentCheckpontIndex;
        return this;
    }

    public QuestBuilder setState(QuestState state) {
        this.state = state;
        return this;

    }

    public Quest build() {
        return new Quest(questID, questName, playerID, state, currentCheckpontIndex, entityMap);
    }

}
