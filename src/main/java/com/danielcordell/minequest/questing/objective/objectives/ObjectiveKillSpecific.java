package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsKillSpecific;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ObjectiveKillSpecific extends ObjectiveBase {

    private String nbtTagToFind;
    private int numToKill;
    private int numKilled;

    public ObjectiveKillSpecific(ParamsKillSpecific params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        nbtTagToFind = params.nbtTagToFind;
        numToKill = params.numToKill;
        numKilled = params.numAlreadyKilled;
    }

    public ObjectiveKillSpecific(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof LivingDeathEvent)) return;
        if (state != QuestState.STARTED) return;
        LivingDeathEvent event = ((LivingDeathEvent) baseEvent);
        Entity entity = event.getEntity();
        if (entity.getEntityData().hasKey(nbtTagToFind)) {
            numKilled++;
            if (numToKill == numKilled) {
                completeObjective(entity.world);
            }
            quest.setDirty();
        }
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setInteger("numToKill", numToKill);
        nbt.setInteger("numKilled", numKilled);
        nbt.setString("nbtTagToFind", nbtTagToFind);
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        numToKill = nbt.getInteger("numToKill");
        numKilled = nbt.getInteger("numKilled");
        nbtTagToFind = nbt.getString("nbtTagToFind");
    }

    @Override
    public String debugInfo() {
        return "Target - Specific Entities" + ": " + numKilled + "/" + numToKill;
    }

}
