package com.danielcordell.minequest.blocks;

import com.danielcordell.minequest.events.ActionBlockTriggeredEvent;
import com.danielcordell.minequest.tileentities.QuestActionTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class QuestActionBlock extends Block implements ITileEntityProvider {
    public QuestActionBlock() {
        super(Material.CLOTH);
        setUnlocalizedName("questactionblock");
        setRegistryName("questactionblock");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new QuestActionTileEntity();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isBlockPowered(pos)) {
            int actionBlockID = ((QuestActionTileEntity) worldIn.getTileEntity(pos)).getActionBlockID();
            MinecraftForge.EVENT_BUS.post(new ActionBlockTriggeredEvent(worldIn, actionBlockID));
        }
    }
}
