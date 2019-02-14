package com.danielcordell.minequest.worlddata;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.quest.Quest;
import com.danielcordell.minequest.quest.QuestBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldQuestData extends WorldSavedData {
    private static final String NAME = MineQuest.MODID.concat("_QUESTDATA");
    private ArrayList<Quest> quests = new ArrayList<>();

    private int currentQuestIDCounter = 0;

    public WorldQuestData() {
        super(NAME);
    }

    public WorldQuestData(String s) {
        super(s);
    }

    public static WorldQuestData get(World world) {
        MapStorage storage = world.getMapStorage();
        WorldQuestData instance = (WorldQuestData) storage.getOrLoadData(WorldQuestData.class, NAME);
        if (instance == null) {
            instance = new WorldQuestData();
            storage.setData(NAME, instance);
        }
        return instance;
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = ((NBTTagList) nbt.getTag("quests"));
        for (NBTBase quest : list) {
            quests.add(QuestBuilder.fromNBT((NBTTagCompound) quest));
        }
        currentQuestIDCounter = nbt.getInteger("currentQuestIDCounter");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Quest quest : quests) {
            list.appendTag(quest.toNBT());
        }
        nbt.setTag("quests", list);
        nbt.setInteger("currentQuestIDCounter", currentQuestIDCounter);
        return nbt;
    }

    public void addQuest(Quest q) {
        quests.add(q);
        markDirty();
    }

    public Quest getQuestByID(int id) {
        return quests.stream().filter(quest -> quest.getQuestID() == id).findFirst().orElse(null);
    }

    public int getFreshQuestID() {
        return currentQuestIDCounter++;
    }

    public List<Quest> getImmutableQuests() {
        return Collections.unmodifiableList(quests);
    }
}