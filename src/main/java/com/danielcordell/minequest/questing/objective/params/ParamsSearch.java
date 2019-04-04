package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;

public class ParamsSearch extends ObjectiveParamsBase {
    public String structureType;

    public ParamsSearch(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsSearch(QuestCheckpoint checkpoint, String description, QuestState state) {
        super(checkpoint, description, state);
    }

    public ParamsSearch setParamDetails(String structureType) {
        this.structureType = structureType;
        return this;
    }

}
