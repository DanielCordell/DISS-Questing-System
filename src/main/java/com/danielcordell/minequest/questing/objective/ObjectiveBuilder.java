package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.objective.objectives.*;
import com.danielcordell.minequest.questing.objective.params.*;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.nbt.NBTTagCompound;

public class ObjectiveBuilder {
    public static ObjectiveBase fromParams(ObjectiveParamsBase base) {
        if (base instanceof ParamsKillType) {
            return new ObjectiveKillType((ParamsKillType) base, ObjectiveType.KILL_TYPE);
        } else if (base instanceof ParamsKillSpecific) {
            return new ObjectiveKillSpecific((ParamsKillSpecific) base, ObjectiveType.KILL_SPECIFIC);
        } else if (base instanceof ParamsGather) {
            return new ObjectiveGather((ParamsGather) base, ObjectiveType.GATHER);
        } else if (base instanceof ParamsTrigger) {
            return new ObjectiveTrigger((ParamsTrigger) base, ObjectiveType.TRIGGER);
        } else if (base instanceof ParamsDeliver) {
            return new ObjectiveDeliver((ParamsDeliver) base, ObjectiveType.DELIVER);
        } else if (base instanceof ParamsEscort) {
            return new ObjectiveEscort((ParamsEscort) base, ObjectiveType.ESCORT);
        } else if (base instanceof ParamsSearch) {
            return new ObjectiveSearch((ParamsSearch) base, ObjectiveType.SEARCH);
        }
        return null;
    }

    public static ObjectiveBase fromNBT(QuestCheckpoint checkpoint, NBTTagCompound nbt) {
        ObjectiveType type = ObjectiveType.getTypeFromInt(nbt.getInteger("type"));
        switch (type) {
            case KILL_TYPE:
                return new ObjectiveKillType(checkpoint, type, nbt);
            case KILL_SPECIFIC:
                return new ObjectiveKillSpecific(checkpoint, type, nbt);
            case GATHER:
                return new ObjectiveGather(checkpoint, type, nbt);
            case TRIGGER:
                return new ObjectiveTrigger(checkpoint, type, nbt);
            case DELIVER:
                return new ObjectiveDeliver(checkpoint, type, nbt);
            case ESCORT:
                return new ObjectiveEscort(checkpoint, type, nbt);
            case SEARCH:
                return new ObjectiveSearch(checkpoint, type, nbt);
        }
        return null;
    }
}
