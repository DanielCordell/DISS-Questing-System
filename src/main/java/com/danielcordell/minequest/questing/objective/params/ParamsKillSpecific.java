package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;

public class ParamsKillSpecific extends ObjectiveParamsBase {

    public String nbtTagToFind;
    public int numToKill;
    public int numAlreadyKilled;

    public ParamsKillSpecific setParamDetails(String nbtTagToFind, int numToKill) {
        return setParamDetails(nbtTagToFind, numToKill, 0);
    }

    public ParamsKillSpecific setParamDetails(String nbtTagToFind, int numToKill, int numAlreadyKilled) {
        this.nbtTagToFind = nbtTagToFind;
        this.numToKill = numToKill;
        this.numAlreadyKilled = numAlreadyKilled;
        return this;
    }

    public ParamsKillSpecific(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsKillSpecific(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        super(questCheckpoint, description, optional);
    }

    public ParamsKillSpecific(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        super(questCheckpoint, description, optional, state);
    }
}