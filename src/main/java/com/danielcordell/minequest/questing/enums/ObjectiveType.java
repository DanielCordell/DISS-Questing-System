package com.danielcordell.minequest.questing.enums;

import com.danielcordell.minequest.questing.generators.WorldState;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashMap;
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

    //Get Default Objective Weights
    public static HashMap<ObjectiveType, Integer> getObjectiveWeightMap(WorldState worldState) {
        HashMap<ObjectiveType, Integer> weight = new HashMap<>();
        weight.put(ObjectiveType.KILL_TYPE, (int) (worldState.nearbySpawners.size() * (0.1 * worldState.nearbyMobs.size())) + (worldState.inOrNextToSlimeChunk ? 2 : 0));
        weight.put(ObjectiveType.KILL_SPECIFIC, worldState.nearbySpawners.size());
        weight.put(ObjectiveType.GATHER, 5);
        weight.put(ObjectiveType.TRIGGER, 0);
        weight.put(ObjectiveType.SEARCH, ((int) worldState.closestStructurePerType.values()
                .stream()
                // If a position exists, and it's < 10000 away from the player, increase the probability of a locate quest.
                .filter(it -> it != null && Math.sqrt(it.first().distanceSq(worldState.playerPos)) < 10000)
                .count())
        );

        boolean isPlayerInsideStructure = worldState.closestStructurePerType.entrySet().stream().anyMatch(it -> it.getValue().second() && it.getKey().equals("Temple") && worldState.inWater);
        weight.put(ObjectiveType.ESCORT, isPlayerInsideStructure ? 8 : 0);
        weight.put(ObjectiveType.DELIVER, isPlayerInsideStructure ? 8 : 0);
        return weight;
    }

}
