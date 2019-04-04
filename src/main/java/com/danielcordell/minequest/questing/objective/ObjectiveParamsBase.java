package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;

public abstract class ObjectiveParamsBase {
    public final QuestCheckpoint checkpoint;

    public String description;
    public final QuestState state;


    public ObjectiveParamsBase(QuestCheckpoint questCheckpoint, String description) {
        this(questCheckpoint, description, QuestState.STARTED);
    }

    public ObjectiveParamsBase(QuestCheckpoint questCheckpoint, String description, QuestState state) {
        this.checkpoint = questCheckpoint;
        this.description = description;
        this.state = state == QuestState.CREATED ? QuestState.STARTED : state;
    }
}
