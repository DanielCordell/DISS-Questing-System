package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.events.ActionBlockTriggeredEvent;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.ParamsTrigger;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;


public class ObjectiveTrigger extends ObjectiveBase {

    private UUID actionBlockID;

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
        nbt.setTag("actionBlockID", NBTUtil.createUUIDTag(actionBlockID));
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        actionBlockID = NBTUtil.getUUIDFromTag((NBTTagCompound) nbt.getTag("actionBlockID"));
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof ActionBlockTriggeredEvent)) return;
        if (state != QuestState.STARTED) return;
        ActionBlockTriggeredEvent event = ((ActionBlockTriggeredEvent) baseEvent);
        if (event.actionBlockID.compareTo(actionBlockID) == 0) {
            completeObjective(event.world);
        }
    }

    @Override
    public String getSPObjectiveInfo(EntityPlayerSP player) {
        return "Interact with Action Block: " + actionBlockID;
    }

    @Override
    public ObjectiveParamsBase getParams() {
        return new ParamsTrigger(checkpoint, getDescription(), optional, state).setParamDetails(actionBlockID);
    }
}
