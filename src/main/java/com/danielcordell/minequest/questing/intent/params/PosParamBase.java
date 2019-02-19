package com.danielcordell.minequest.questing.intent.params;

import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PosParamBase {
    public static PosParamBase fromNBT(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        if (type.equals("radius")) return new PlayerRadiusPosParam(nbt.getInteger("radius"));
        return null;
    }

    public abstract BlockPos getPos(World world, Quest quest);

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("type", getParamTypeString());
        return toParamSpecificNBT(nbt);
    }

    public abstract String getParamTypeString();

    public abstract NBTTagCompound toParamSpecificNBT(NBTTagCompound nbt);
}
