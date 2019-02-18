package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;

public abstract class ObjectiveParamsBase {
    public final QuestCheckpoint checkpoint;

    public final String description;
    public final QuestState state;
    public final boolean optional;

    public ObjectiveParamsBase(QuestCheckpoint questCheckpoint, String description) {
        this(questCheckpoint, description, false);
    }

    public ObjectiveParamsBase(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        this(questCheckpoint, description, optional, QuestState.STARTED);
    }

    public ObjectiveParamsBase(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        this.checkpoint = questCheckpoint;
        this.description = description;
        this.optional = optional;
        this.state = state == QuestState.CREATED ? QuestState.STARTED : state;
    }
}
