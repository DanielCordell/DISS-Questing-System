package com.danielcordell.minequest.questing.enums;

import java.util.Arrays;
import java.util.Optional;

public enum QuestState {
    CREATED(0),
    STARTED(1),
    COMPLETED(2),
    FAILED(3);

    public final int stateInt;

    QuestState(int stateInt) {
        this.stateInt = stateInt;
    }

    public static QuestState getStateFromInt(int type) {
        Optional<QuestState> state = Arrays.stream(QuestState.values())
                .filter(value -> value.stateInt == type)
                .findFirst();
        if (!state.isPresent()) throw new IllegalArgumentException("Invalid state value passed to QuestState.getTypeFromInt() : " + type);
        return state.get();
    }

}
