package com.danielcordell.minequest.questing.quest;

import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;

public class QuestCheckpoint {
    private final Quest quest;
    private final boolean isFinalCheckpoint;

    private final ArrayList<ObjectiveBase> objectives;

    public QuestCheckpoint(Quest quest) {
        this(quest, new NBTTagCompound());
    }

    //Adds to quest automatically
    public QuestCheckpoint(Quest quest, NBTTagCompound nbt) {
        this.quest = quest;
        this.isFinalCheckpoint = nbt.getBoolean("isFinalCheckpoint");
        quest.addCheckpoint(this);
        
        objectives = new ArrayList<>();
        if (!nbt.hasKey("objectives")) return;
        NBTTagList list = ((NBTTagList) nbt.getTag("objectives"));
        list.forEach(nbtBase -> objectives.add(ObjectiveBuilder.fromNBT(this, (NBTTagCompound) nbtBase)));
    }

    public NBTBase toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("isFinalCheckpoint", isFinalCheckpoint);
        NBTTagList list = new NBTTagList();
        objectives.forEach(objective -> list.appendTag(objective.toNBT()));
        nbt.setTag("objectives", list);

        return nbt;
    }

    public Quest getQuest() {
        return quest;
    }

    public void addObjective(ObjectiveBase objective) {
        objectives.add(objective);
    }

    public ArrayList<ObjectiveBase> getObjectives() {
        return new ArrayList<>(objectives);
    }
}
