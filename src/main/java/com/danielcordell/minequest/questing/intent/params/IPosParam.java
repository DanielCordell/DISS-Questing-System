package com.danielcordell.minequest.questing.intent.params;

import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPosParam {
    static IPosParam fromNBT(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        if (type.equals("radius")) return new PlayerRadiusPosParam(nbt.getInteger("radius"));
        return null;
    }

    public BlockPos getPos(World world, Quest quest);

    public NBTTagCompound toNBT();
}
