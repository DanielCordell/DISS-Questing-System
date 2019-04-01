package com.danielcordell.minequest.blocks;

import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class QuestStartBlock extends Block implements ITileEntityProvider {
    public QuestStartBlock() {
        super(Material.CLOTH);
        setUnlocalizedName("queststartblock");
        setRegistryName("queststartblock");
        setBlockUnbreakable();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new QuestStartTileEntity();
    }
}
