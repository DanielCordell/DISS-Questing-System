package com.danielcordell.minequest.questing.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ObjectiveType {
    KILLALL(0);

    public final int objectiveInt;

    ObjectiveType(int objectiveInt) {
        this.objectiveInt = objectiveInt;
    }

    public static ObjectiveType getTypeFromInt(int type) {
        Optional<ObjectiveType> objective = Arrays.stream(ObjectiveType.values())
                .filter(value -> value.objectiveInt == type)
                .findFirst();
        if (!objective.isPresent()) throw new IllegalArgumentException("Invalid state value passed to QuestState.getTypeFromInt() : " + type);
        return objective.get();
    }
}
