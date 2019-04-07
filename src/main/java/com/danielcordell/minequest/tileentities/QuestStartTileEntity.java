package com.danielcordell.minequest.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class QuestStartTileEntity extends TileEntity {

    private int questID = -1;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("questID"))
            questID = compound.getInteger("questID");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("questID", questID);
        return super.writeToNBT(compound);
    }

    public int getQuestID() {
        return questID;
    }

    public boolean isAssignedQuest() {
        return questID != -1;
    }

    public void setQuestID(int questID) {
        this.questID = questID;
    }
}
