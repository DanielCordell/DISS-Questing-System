package com.danielcordell.minequest.questing.quest;

import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.intent.IntentBuilder;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuestCheckpoint {
    private final Quest quest;
    private final boolean isFinalCheckpoint;

    private final ArrayList<ObjectiveBase> objectives;

    private final ArrayList<Intent> intents;

    public QuestCheckpoint(Quest quest) {
        this(quest, new NBTTagCompound());
    }

    //Adds to quest automatically
    public QuestCheckpoint(Quest quest, NBTTagCompound nbt) {
        this.quest = quest;
        this.isFinalCheckpoint = nbt.getBoolean("isFinalCheckpoint");
        quest.addCheckpoint(this);
        
        objectives = new ArrayList<>();
        intents = new ArrayList<>();
        if (nbt.hasKey("objectives")) {
            NBTTagList list = ((NBTTagList) nbt.getTag("objectives"));
            list.forEach(nbtBase -> objectives.add(ObjectiveBuilder.fromNBT(this, (NBTTagCompound) nbtBase)));
        }
        if (nbt.hasKey("intents")) {
            NBTTagList list = ((NBTTagList) nbt.getTag("intents"));
            list.forEach(nbtBase -> intents.add(IntentBuilder.fromNBT(quest, (NBTTagCompound) nbtBase)));
        }
    }

    public NBTBase toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("isFinalCheckpoint", isFinalCheckpoint);
        NBTTagList objList = new NBTTagList();
        objectives.forEach(objective -> objList.appendTag(objective.toNBT()));
        nbt.setTag("objectives", objList);
        NBTTagList intentList = new NBTTagList();
        intents.forEach(intent -> intentList.appendTag(intent.toNBT()));
        nbt.setTag("intents", intentList);

        return nbt;
    }

    public Quest getQuest() {
        return quest;
    }

    public void addObjective(ObjectiveBase objective) {
        objectives.add(objective);
    }

    public void addIntent(Intent intent) {
        intents.add(intent);
    }


    public ArrayList<ObjectiveBase> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public void performIntents(World world) {
        intents.forEach(intent -> intent.perform(world));
    }
}
