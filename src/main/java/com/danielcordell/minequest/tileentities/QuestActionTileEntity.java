package com.danielcordell.minequest.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class QuestActionTileEntity extends TileEntity {

    private int actionBlockID = -1;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        actionBlockID = compound.getInteger("actionBlockID");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("actionBlockID", actionBlockID);
        return compound;
    }

    public int getActionBlockID() {
        return actionBlockID;
    }

    public boolean isAssignedTrigger() {
        return actionBlockID != -1;
    }

    public void setActionBlockID(int questID) {
        this.actionBlockID = questID;
    }
}
