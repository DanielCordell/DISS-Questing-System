package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsGather;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;


public class ObjectiveGather extends ObjectiveBase {

    private ItemStack item;
    private int count;

    public ObjectiveGather(ParamsGather params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        item = params.item;
        count = params.count;
    }

    public ObjectiveGather(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setTag("item", item.serializeNBT());
        nbt.setInteger("count", count);
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        item = new ItemStack((NBTTagCompound) nbt.getTag("item"));
        count = nbt.getInteger("count");
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof PlayerTickEvent)) return;
        if (state != QuestState.STARTED) return;
        PlayerTickEvent event = ((PlayerTickEvent) baseEvent);

        int currentCount = 0;
        InventoryPlayer inv = event.player.inventory;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.getItem() == item.getItem())
                currentCount += itemStack.getCount();
        }
        if (currentCount == count) {
            completeObjective(event.player.world);
            quest.setDirty();
        }
    }

    @Override
    public String debugInfo() {
        return "Target - Gather " + count + " " + I18n.format(item.getUnlocalizedName() + ".name") ;
    }
}
