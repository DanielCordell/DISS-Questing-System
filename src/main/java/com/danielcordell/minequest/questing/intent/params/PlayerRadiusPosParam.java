package com.danielcordell.minequest.questing.intent.params;

import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerRadiusPosParam implements IPosParam{

    private int radius;

    public PlayerRadiusPosParam(int radius) {
        this.radius = radius;
    }

    //Pushes to the surface just in case.
    @Override
    public BlockPos getPos(World world, Quest quest) {
        BlockPos playerPos = world.getPlayerEntityByUUID(quest.getPlayerID()).getPosition();
        double angle = Math.random() * 2 * Math.PI;
        BlockPos pos = new BlockPos(playerPos.getX() + radius * Math.cos(angle), ((double) playerPos.getY()), playerPos.getZ() + radius * Math.sin(angle));
        return world.getTopSolidOrLiquidBlock(pos).add(0, 2, 0);
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("radius", radius);
        return nbt;
    }
}
