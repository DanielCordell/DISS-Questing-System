package com.danielcordell.minequest.questing.intent;

import com.danielcordell.minequest.questing.enums.IntentType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class Intent {
    public abstract void perform(World world);

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("type", getIntentType().intentInt);
        return toIntentSpecificNBT(nbt);
    }
    public abstract NBTTagCompound toIntentSpecificNBT(NBTTagCompound nbt);
    public abstract IntentType getIntentType();
}