package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.ParamsKillType;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ObjectiveKillType extends ObjectiveBase {
    private Class<? extends EntityLivingBase> entityType;
    private int numToKill;
    private int numKilled;

    public ObjectiveKillType(ParamsKillType params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        entityType = params.entityTypeToKill;
        numToKill = params.numToKill;
        numKilled = params.numAlreadyKilled;
    }

    public ObjectiveKillType(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof LivingDeathEvent)) return;
        if (state != QuestState.STARTED) return;
        LivingDeathEvent event = ((LivingDeathEvent) baseEvent);
        if (event.getEntity().getClass() == entityType) {
            numKilled++;
            if (numToKill == numKilled) {
                completeObjective(event.getEntity().world);
            }
            quest.setDirty();
        }
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setInteger("numToKill", numToKill);
        nbt.setInteger("numKilled", numKilled);
        nbt.setString("entityName", Util.getNameFromEntity(entityType));
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        numToKill = nbt.getInteger("numToKill");
        numKilled = nbt.getInteger("numKilled");
        entityType = Util.getEntityFromName(nbt.getString("entityName"));
    }

    @Override
    public String getSPObjectiveInfo(EntityPlayerSP player) {
        return Util.getPrintableNameFromEntity(entityType) + ": " + numKilled + "/" + numToKill;
    }

    @Override
    public ObjectiveParamsBase getParams() {
        return new ParamsKillType(checkpoint, getDescription(), optional, state).setParamDetails(entityType, numToKill);
    }

}
