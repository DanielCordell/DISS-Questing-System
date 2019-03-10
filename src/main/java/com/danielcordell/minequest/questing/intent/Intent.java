package com.danielcordell.minequest.questing.intent;

import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class Intent {
    public Intent(Quest quest) {
        this.quest = quest;
    }

    //Does not need to be saved.
    protected Quest quest;

    public abstract void perform(World world);

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("type", getIntentType().intentInt);
        return toIntentSpecificNBT(nbt);
    }

    public abstract NBTTagCompound toIntentSpecificNBT(NBTTagCompound nbt);

    public abstract IntentType getIntentType();
}