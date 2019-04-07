package com.danielcordell.minequest.questing.enums;

import java.util.Arrays;
import java.util.Optional;

public enum IntentType {
    SPAWN_ENTITY(0),
    GIVE_ITEMSTACK(1),
    SET_NPC_FOLLOW(2);

    public final int intentInt;

    IntentType(int objectiveInt) {
        this.intentInt = objectiveInt;
    }

    public static IntentType getTypeFromInt(int type) {
        Optional<IntentType> objective = Arrays.stream(IntentType.values())
                .filter(value -> value.intentInt == type)
                .findFirst();
        if (!objective.isPresent())
            throw new IllegalArgumentException("Invalid state value passed to IntentType.getTypeFromInt() : " + type);
        return objective.get();
    }
}
