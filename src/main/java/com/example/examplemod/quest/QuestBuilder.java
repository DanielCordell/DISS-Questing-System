package com.example.examplemod.quest;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class QuestBuilder {
    private int questID;
    private String questName;
    private UUID playerID;
    private int currentCheckpontIndex = 0;
    private QuestState state = QuestState.CREATED;

    private HashMap<Integer, EntityLiving> entityMap = null;

    public static Quest fromNBT(NBTTagCompound nbt, World world) {
        QuestBuilder qb = new QuestBuilder(nbt.getInteger("questID"),nbt.getString("questName"), nbt.getUniqueId("playerID"));
        qb.setCurrentCheckpontIndex(nbt.getInteger("currentCheckpointIndex"))
                .setState(QuestState.getStateFromInt(nbt.getInteger("state")));

        NBTTagCompound entityMapNBT = nbt.getCompoundTag("entityMap");
        entityMapNBT.getKeySet().forEach(questEntityID -> {
            int entityID = entityMapNBT.getInteger(questEntityID);
            qb.addEntity(Integer.valueOf(questEntityID), ((EntityLiving) world.getEntityByID(entityID)));
        });
        return qb.build();
    }

    QuestBuilder(int questID, String questName, UUID playerID) {
        this.questID = questID;
        this.questName = questName;
        this.playerID = playerID;
    }

    public QuestBuilder setCurrentCheckpontIndex(int currentCheckpontIndex) {
        this.currentCheckpontIndex = currentCheckpontIndex;
        return this;
    }

    public QuestBuilder setState(QuestState state) {
        this.state = state;
        return this;

    }

    public QuestBuilder addEntity(int questEntityID, EntityLiving entity) {
        if (entityMap == null) entityMap = new HashMap<>();
        entityMap.put(questEntityID, entity);
        return this;
    }

    public QuestBuilder addEntities(HashMap<Integer, EntityLiving> entities){
        this.entityMap = entities;
        return this;
    }

    public Quest build() {
        return new Quest(questID, questName, playerID, state, entityMap, currentCheckpontIndex);
    }

}
