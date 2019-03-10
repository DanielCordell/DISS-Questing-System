package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;

public class ParamsTrigger extends ObjectiveParamsBase {
    public int actionBlockID;

    public ParamsTrigger(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsTrigger(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        super(questCheckpoint, description, optional);
    }

    public ParamsTrigger(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        super(questCheckpoint, description, optional, state);
    }

    public ParamsTrigger setParamDetails(int actionBlockID) {
        this.actionBlockID = actionBlockID;
        return this;
    }
}
