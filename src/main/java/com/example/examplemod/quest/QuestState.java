package com.example.examplemod.quest;

import com.sun.javaws.exceptions.InvalidArgumentException;

public enum QuestState {
    CREATED(0),
    STARTED(1),
    COMPLETED(2),
    FAILED(3);

    final int stateInt;

    QuestState(int stateInt) {
        this.stateInt = stateInt;
    }


    public static QuestState getStateFromInt(int state) {
        if (state == CREATED.stateInt) return CREATED;
        if (state == STARTED.stateInt) return STARTED;
        if (state == COMPLETED.stateInt) return COMPLETED;
        if (state == FAILED.stateInt) return FAILED;
        throw new IllegalArgumentException("Invalid state value passed to QuestState.getStateFromInt() : " + state);
    }

}
