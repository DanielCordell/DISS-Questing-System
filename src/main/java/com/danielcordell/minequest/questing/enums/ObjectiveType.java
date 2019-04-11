package com.danielcordell.minequest.questing.enums;

import com.danielcordell.minequest.questing.generators.WorldState;
import com.danielcordell.minequest.questing.objective.objectives.ObjectiveTrigger;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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

    public static ConcurrentHashMap<ObjectiveType, Integer> getObjectiveWeightMap() {
        ConcurrentHashMap<ObjectiveType, Integer> weight = new ConcurrentHashMap<>();
        weight.put(ObjectiveType.KILL_TYPE, 0);
        weight.put(ObjectiveType.KILL_SPECIFIC, 0);
        weight.put(ObjectiveType.GATHER, 0);
        weight.put(ObjectiveType.TRIGGER, 0);
        weight.put(ObjectiveType.SEARCH, 0);
        weight.put(ObjectiveType.ESCORT,  0);
        weight.put(ObjectiveType.DELIVER, 0);
        return weight;
    }
    //Get Default Objective Weights
    public static ConcurrentHashMap<ObjectiveType, Integer> getObjectiveWeightMap(WorldState worldState) {
        ConcurrentHashMap<ObjectiveType, Integer> weight = getObjectiveWeightMap();
        int baseVal = (int) ((worldState.nearbySpawners.size() + 1) * (0.1 * worldState.nearbyMobs.stream().filter(it -> it instanceof IMob).count())) + (worldState.inOrNextToSlimeChunk ? 2 : 0);
        weight.put(ObjectiveType.KILL_TYPE, baseVal < 6 ? 6 : baseVal > 12 ? 12 : baseVal);
        weight.put(ObjectiveType.KILL_SPECIFIC, weight.get(ObjectiveType.KILL_TYPE)/2);
        weight.put(ObjectiveType.GATHER, 6);
        weight.put(ObjectiveType.TRIGGER, 0);
        HashMap<String, Pair<BlockPos, Boolean>> closestStructurePerType = worldState.closestStructurePerType;
        int searchWeight = (int) closestStructurePerType.values()
                .stream()
                // If a position exists, and it's < 1000 away from the player, increase the probability of a locate quest.
                .filter(it -> it != null && Math.sqrt(it.first().distanceSq(worldState.playerPos)) < 1000)
                .count();
        searchWeight = (int) Math.ceil(searchWeight * 1.5);
        weight.put(ObjectiveType.SEARCH, (searchWeight));
        int escortWeight = worldState.dimension != DimensionType.OVERWORLD.getId() ? 8 : closestStructurePerType.get("Village").second() ? 40 : 0;
        weight.put(ObjectiveType.ESCORT,  escortWeight);

        // If inside EVERY structure (it can happen) don't generate these types of objective.
        if (closestStructurePerType.values().stream().allMatch(Pair::second)) {
            weight.put(ObjectiveType.SEARCH, 0);
            weight.put(ObjectiveType.ESCORT, 0);
        }

        weight.put(ObjectiveType.DELIVER, 5);
        return weight;
    }

}
