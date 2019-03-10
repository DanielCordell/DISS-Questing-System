package com.danielcordell.minequest.questing.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public enum ObjectiveType {
    KILL_TYPE(0),
    KILL_SPECIFIC(1),
    GATHER(2),
    TRIGGER(3),
    DELIVER(4),
    ESCORT(5),
    SEARCH(6);

    public final int objectiveInt;

    ObjectiveType(int objectiveInt) {
        this.objectiveInt = objectiveInt;
    }

    public static ObjectiveType getTypeFromInt(int type) {
        Optional<ObjectiveType> objective = Arrays.stream(values())
                .filter(value -> value.objectiveInt == type)
                .findFirst();
        if (!objective.isPresent())
            throw new IllegalArgumentException("Invalid state value passed to ObjectiveType.getTypeFromInt() : " + type);
        return objective.get();
    }

    public static ObjectiveType getRandomObjectiveType(Random rand) {
        ObjectiveType[] vals = values();
        return vals[rand.nextInt(vals.length)];
    }

}
