package com.danielcordell.minequest.blocks;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class QuestStartBlock extends Block implements ITileEntityProvider {

    public QuestStartBlock() {
        super(Material.CLOTH);
        setUnlocalizedName(MineQuest.MODID + ".queststartblock");
        setRegistryName("queststartblock");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new QuestStartTileEntity();
    }

    private QuestStartTileEntity getTE(World world, BlockPos pos) {
        return (QuestStartTileEntity) world.getTileEntity(pos);
    }
}
