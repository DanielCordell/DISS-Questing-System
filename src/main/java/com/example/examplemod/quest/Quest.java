package com.example.examplemod.quest;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class Quest {
    private int questID;
    private String questName;
    private UUID playerID;

    private int currentCheckpontIndex;
    private QuestState state;

    private HashMap<Integer, EntityLiving> entityMap;

    public Quest(NBTTagCompound nbt, World world) {
        questID = nbt.getInteger("questID");
        questName = nbt.getString("questName");
        playerID = nbt.getUniqueId("playerID");
        currentCheckpontIndex = nbt.getInteger("currentCheckpointIndex");
        state = QuestState.getStateFromInt(nbt.getInteger("state"));

        NBTTagCompound entityMapNBT = nbt.getCompoundTag("entityMap");
        entityMap = new HashMap<>();
        entityMapNBT.getKeySet().forEach(questEntityID -> {
            int entityID = entityMapNBT.getInteger(questEntityID);
            entityMap.put(Integer.valueOf(questEntityID), ((EntityLiving) world.getEntityByID(entityID)));
        });
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("questID", questID);
        nbt.setString("questName", questName);
        nbt.setUniqueId("playerID", playerID);
        nbt.setInteger("currentCheckpointIndex", currentCheckpontIndex);
        nbt.setInteger("state", state.stateInt);

        NBTTagCompound entityMapNBT = new NBTTagCompound();
        entityMap.forEach((questEntityID, entity) -> entityMapNBT.setInteger(questEntityID.toString(), entity.getEntityId()));
        nbt.setTag("entityMap", entityMapNBT);

        return nbt;
    }




    Quest(int questID, String questName, UUID playerID, QuestState state, HashMap<Integer, EntityLiving> entityMap, int currentCheckpontIndex) {
        this.questID = questID;
        this.questName = questName;
        this.playerID = playerID;
        this.currentCheckpontIndex = currentCheckpontIndex;
        this.state = state;
        this.entityMap = entityMap;
    }






}
