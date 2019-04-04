package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;

import java.util.UUID;

public class ParamsTrigger extends ObjectiveParamsBase {
    public UUID actionBlockID;

    public ParamsTrigger(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsTrigger(QuestCheckpoint checkpoint, String description, QuestState state) {
        super(checkpoint, description, state);
    }

    public ParamsTrigger setParamDetails(UUID actionBlockID) {
        this.actionBlockID = actionBlockID;
        return this;
    }
}
