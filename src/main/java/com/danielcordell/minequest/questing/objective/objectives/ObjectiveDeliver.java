package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsDeliver;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;


public class ObjectiveDeliver extends ObjectiveBase {
    private ItemStack item;
    private int count;
    private int questEntityID;

    public ObjectiveDeliver(ParamsDeliver params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        item = params.item;
        count = params.count;
        questEntityID = params.questEntityID;
    }

    public ObjectiveDeliver(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setTag("item", item.serializeNBT());
        nbt.setInteger("count", count);
        nbt.setInteger("questEntityID", questEntityID);
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        item = new ItemStack((NBTTagCompound) nbt.getTag("item"));
        count = nbt.getInteger("count");
        questEntityID = nbt.getInteger("questEntityID");
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof EntityInteract)) return;
        if (state != QuestState.STARTED) return;
        EntityInteract event = ((EntityInteract) baseEvent);

        Entity target = event.getTarget();
        // If entity target is not san NPC stop.
        if (!(event.getTarget() instanceof EntityNPC)) return;
        // If this objective is not for this NPC then stop.
        if (quest.getQuestEntityIDFromEntityID(target.getEntityId()) != questEntityID) return;

        HashMap<Integer, Integer> currentCount = new HashMap<>();
        InventoryPlayer inv = event.getEntityPlayer().inventory;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.getItem() == item.getItem())
                currentCount.put(i, itemStack.getCount());
        }
        if (currentCount.values().stream().mapToInt(i -> i).sum() == count) {
            currentCount.keySet().forEach(inv::removeStackFromSlot);
            completeObjective(event.getWorld());
            quest.setDirty();
        }
        else {
            event.getEntityPlayer().sendMessage(new TextComponentString("You do not have enough " + I18n.format(item.getUnlocalizedName())));
        }
    }

    @Override
    public String debugInfo() {
        return "Target - Give " + count + " " + I18n.format(item.getUnlocalizedName() + ".name") + " to NPC.";
    }
}