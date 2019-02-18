package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.EntityLivingBase;

public class ParamsKillType extends ObjectiveParamsBase{

    public Class<? extends EntityLivingBase> entityTypeToKill;
    public int numToKill;
    public int numAlreadyKilled;

    public ParamsKillType setQuestDetails(Class<? extends EntityLivingBase> entityTypeToKill, int numToKill) {
        return setQuestDetails(entityTypeToKill, numToKill, 0);
    }

    public ParamsKillType setQuestDetails(Class<? extends EntityLivingBase> entityTypeToKill, int numToKill, int numAlreadyKilled) {
        this.entityTypeToKill = entityTypeToKill;
        this.numToKill = numToKill;
        this.numAlreadyKilled = numAlreadyKilled;
        return this;
    }

    public ParamsKillType(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsKillType(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        super(questCheckpoint, description, optional);
    }

    public ParamsKillType(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        super(questCheckpoint, description, optional, state);
    }
}