package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.EntityLivingBase;

public class ParamsKillType extends ObjectiveParamsBase {

    public Class<? extends EntityLivingBase> entityTypeToKill;
    public int numToKill;
    public int numAlreadyKilled;

    public ParamsKillType(QuestCheckpoint checkpoint, String description, QuestState state) {
        super(checkpoint, description, state);
    }

    public ParamsKillType setParamDetails(Class<? extends EntityLivingBase> entityTypeToKill, int numToKill) {
        return setParamDetails(entityTypeToKill, numToKill, 0);
    }

    public ParamsKillType setParamDetails(Class<? extends EntityLivingBase> entityTypeToKill, int numToKill, int numAlreadyKilled) {
        this.entityTypeToKill = entityTypeToKill;
        this.numToKill = numToKill;
        this.numAlreadyKilled = numAlreadyKilled;
        return this;
    }

    public ParamsKillType(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }
}