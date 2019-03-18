package com.danielcordell.minequest.worldgenerator;

import com.danielcordell.minequest.core.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;

import java.util.UUID;


public class DungeonTemplateProcessor implements ITemplateProcessor {
    private final int questID;
    private final UUID actionBlockID;

    public DungeonTemplateProcessor(int questID, UUID uuid) {
        this.questID = questID;
        this.actionBlockID = uuid;
    }

    @Override
    public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
        if (blockInfo.blockState == Blocks.MAGMA.getDefaultState()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("questID", questID);
            return new Template.BlockInfo(blockInfo.pos, ModBlocks.questStartBlock.getDefaultState(), nbt);
        }
        else if (blockInfo.blockState == Blocks.PRISMARINE.getDefaultState()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setTag("actionBlockID", NBTUtil.createUUIDTag(actionBlockID));
            return new Template.BlockInfo(blockInfo.pos, ModBlocks.questActionBlock.getDefaultState(), nbt);
        }
        else return blockInfo;
    }
}
