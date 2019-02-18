package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import com.danielcordell.minequest.questing.enums.QuestState;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ObjectiveBase {
    //Parent objects
    protected QuestCheckpoint checkpoint;
    protected Quest quest;

    //Objective Data
    protected String description;
    protected QuestState state;
    protected boolean optional;
    protected ObjectiveType type;

    ObjectiveBase(QuestCheckpoint checkpoint, String description, QuestState state, boolean optional, ObjectiveType type) {
        this.checkpoint = checkpoint;
        this.quest = checkpoint.getQuest();
        this.description = description;
        this.state = state;
        this.optional = optional;
        this.type = type;
    }

    ObjectiveBase(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        this(checkpoint, nbt.getString("description"), QuestState.getStateFromInt(nbt.getInteger("state")),
                nbt.getBoolean("optional"), type);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("description", description);
        nbt.setInteger("state", state.stateInt);
        nbt.setBoolean("optional", optional);
        nbt.setInteger("type", type.objectiveInt);
        return objectiveSpecificToNBT(nbt);
    }

    protected abstract NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt);

    public abstract void objectiveSpecificFromNBT(NBTTagCompound nbt);

    public QuestCheckpoint getCheckpoint() {
        return checkpoint;
    }
    public Quest getQuest() {
        return quest;
    }

    public ObjectiveType getType() {
        return type;
    }

    public QuestState getState() {
        return state;
    }

    public abstract String debugInfo();
}
