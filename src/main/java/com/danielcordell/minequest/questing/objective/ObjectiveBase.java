package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.QuestCheckpoint;
import com.danielcordell.minequest.questing.enums.QuestState;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ObjectiveBase {
    //Parent objects
    private QuestCheckpoint checkpoint;
    private Quest quest;

    //Objective Data
    private String description;
    private QuestState state;
    private boolean optional;
    private ObjectiveType type;

    ObjectiveBase(QuestCheckpoint checkpoint, String description, QuestState state, boolean optional) {
        this.checkpoint = checkpoint;
        this.quest = checkpoint.getQuest();
        this.description = description;
        this.state = state;
        this.optional = optional;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("description", description);
        nbt.setInteger("state", state.stateInt);
        nbt.setBoolean("optional", optional);
        return objectiveSpecificToNBT(nbt);
    }

    protected abstract NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt);


    public QuestCheckpoint getCheckpoint() {
        return checkpoint;
    }
    public Quest getQuest() {
        return quest;
    }
}
