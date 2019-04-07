package com.danielcordell.minequest.tileentities;

import com.danielcordell.minequest.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class QuestActionTileEntity extends TileEntity {
    private UUID actionBlockID = Util.emptyUUID;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("actionBlockID"))
            actionBlockID = NBTUtil.getUUIDFromTag((NBTTagCompound) compound.getTag("actionBlockID"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {

        compound.setTag("actionBlockID", NBTUtil.createUUIDTag(actionBlockID));
        return super.writeToNBT(compound);
    }

    public boolean isAssignedTrigger() {
        return actionBlockID.compareTo(Util.emptyUUID) == 0;
    }

    public void setActionBlockID(UUID questID) {
        this.actionBlockID = questID;
    }

    public UUID getActionBlockID() {
        return actionBlockID;
    }
}
