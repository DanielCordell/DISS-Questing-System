package com.danielcordell.minequest.questing.intent.intents;

import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


public class IntentGiveItemStack extends Intent {
    private ItemStack itemStack;

    public IntentGiveItemStack(Quest quest, ItemStack itemStack) {
        super(quest);
        this.itemStack = itemStack;
    }

    public IntentGiveItemStack(NBTTagCompound nbt, Quest quest) {
        this(quest, new ItemStack((NBTTagCompound) nbt.getTag("itemStack")));

    }

    @Override
    public void perform(World world) {
        EntityPlayer player = world.getPlayerEntityByUUID(quest.getPlayerID());
        if (player == null) return;
        if (!player.inventory.addItemStackToInventory(itemStack)) {
            player.dropItem(itemStack, true, false);
        }
    }

    @Override
    public NBTTagCompound toIntentSpecificNBT(NBTTagCompound nbt) {
        nbt.setTag("itemStack", itemStack.serializeNBT());
        return nbt;
    }

    @Override
    public IntentType getIntentType() {
        return IntentType.GIVE_ITEMSTACK;
    }
}
