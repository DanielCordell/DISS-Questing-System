package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.events.ActionBlockTriggeredEvent;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsTrigger;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ObjectiveTrigger extends ObjectiveBase {

    private int actionBlockID;

    public ObjectiveTrigger(ParamsTrigger params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        actionBlockID = params.actionBlockID;
    }

    public ObjectiveTrigger(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setInteger("actionBlockID", actionBlockID);
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        actionBlockID = nbt.getInteger("actionBlockID");
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof ActionBlockTriggeredEvent)) return;
        if (state != QuestState.STARTED) return;
        ActionBlockTriggeredEvent event = ((ActionBlockTriggeredEvent) baseEvent);
        if (event.actionBlockID == actionBlockID) {
            completeObjective(event.world);
        }
    }

    @Override
    public String debugInfo() {
        return "Target - Interact with Action Block: " + actionBlockID;
    }
}
