package com.danielcordell.minequest.questing.quest;

import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.intent.IntentBuilder;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public class QuestCheckpoint {
    private final Quest quest;

    private final ArrayList<ObjectiveBase> objectives;

    private final ArrayList<Intent> intents;

    public QuestCheckpoint(Quest quest) {
        this(quest, new NBTTagCompound());
    }

    public QuestCheckpoint(Quest quest, NBTTagCompound nbt) {
        this.quest = quest;

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

    public void removeObjectivesIf(Predicate<? super ObjectiveBase> filter){
        objectives.removeIf(filter);
    }

    public void performIntents(World world) {
        intents.forEach(intent -> intent.perform(world));
    }

    public ImmutableList<Intent> getIntents() {
        return ImmutableList.copyOf(intents);
    }
}
