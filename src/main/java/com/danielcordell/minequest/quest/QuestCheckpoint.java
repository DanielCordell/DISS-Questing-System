package com.danielcordell.minequest.quest;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class QuestCheckpoint {
    private final Quest quest;
    private final boolean isFinalCheckpoint;

    public QuestCheckpoint(Quest quest) {
        this(quest, false);
    }

    public QuestCheckpoint(Quest quest, boolean isFinalCheckpoint) {
        this.quest = quest;
        this.isFinalCheckpoint = isFinalCheckpoint;
    }

    public NBTBase toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("isFinalCheckpoint", isFinalCheckpoint);
        return nbt;
    }
}
