package com.danielcordell.minequest.questing.intent.params;

import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerRadiusPosParam extends PosParamBase {

    private int radius;

    public PlayerRadiusPosParam(int radius) {
        this.radius = radius;
    }

    //Pushes to the surface just in case.
    @Override
    public BlockPos getPos(World world, Quest quest) {
        BlockPos playerPos = world.getPlayerEntityByUUID(quest.getPlayerID()).getPosition();
        double angle = Math.random() * 2 * Math.PI;
        BlockPos pos;
        int count = 0;
        do {
            if (radius == 0) {
                pos = playerPos;
                break;
            }

            pos = new BlockPos(playerPos.getX() + radius * Math.cos(angle), ((double) playerPos.getY()), playerPos.getZ() + radius * Math.sin(angle));
            if (count == 15) {
                --radius;
                count = 0;
                continue;
            }
            ++count;
        } while (Util.getFirstSolidBelow(world, pos) == Blocks.LAVA.getDefaultState() || (world.getBlockState(pos) != Blocks.AIR && world.getBlockState(pos.add(0,1,0)) != Blocks.AIR));
        return pos;
    }

    @Override
    public String getParamTypeString() {
        return "radius";
    }

    @Override
    public NBTTagCompound toParamSpecificNBT(NBTTagCompound nbt) {
        nbt.setInteger("radius", radius);
        return nbt;
    }
}
