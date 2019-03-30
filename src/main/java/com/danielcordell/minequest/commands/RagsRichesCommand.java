package com.danielcordell.minequest.commands;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RagsRichesCommand extends CommandBase {
    @Override
    public String getName() {
        return "ragsriches";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "ragsriches";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("ragsriches");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = ((EntityPlayer) sender);

        player.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        player.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        player.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
        player.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
        InventoryPlayer inventory = player.inventory;
        inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND_SWORD));
        inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND_PICKAXE));
        inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND_SHOVEL));
        inventory.addItemStackToInventory(new ItemStack(Items.GOLDEN_APPLE,10 ));
        inventory.addItemStackToInventory(new ItemStack(Items.BOW));
        inventory.addItemStackToInventory(new ItemStack(Items.ARROW, 64));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
