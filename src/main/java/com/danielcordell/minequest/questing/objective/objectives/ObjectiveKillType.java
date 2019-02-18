package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsKillType;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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

    public void update(LivingDeathEvent event) {
        if (state != QuestState.STARTED) return;
        if (event.getEntity().getClass() == entityType) {
            numKilled++;
            if (numToKill == numKilled) {
                state = QuestState.COMPLETED;
            }
            quest.setDirty();
        }
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setInteger("numToKill", numToKill);
        nbt.setInteger("numKilled", numKilled);
        nbt.setString("entityName", EntityRegistry.getEntry(entityType).getName());
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        numToKill = nbt.getInteger("numToKill");
        numKilled = nbt.getInteger("numKilled");
        entityType = EntityList.getClassFromName(nbt.getString("entityName")).asSubclass(EntityLiving.class);
    }

    @Override
    public String debugInfo() {
        return "Target - " + EntityRegistry.getEntry(entityType).getName() + ": " + numKilled + "/" + numToKill;
    }

}
