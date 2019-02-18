package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.objective.params.ParamsKillType;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.nbt.NBTTagCompound;

public class ObjectiveBuilder {
    public static ObjectiveBase fromParams(ObjectiveParamsBase base, ObjectiveType type) {
        switch (type) {
            case KILL_TYPE: return new ObjectiveKillType((ParamsKillType) base, type);
            case KILL_SPECIFIC: break;
        }
        return null;
    }

    public static ObjectiveBase fromNBT(QuestCheckpoint checkpoint, NBTTagCompound nbt) {
        ObjectiveType type = ObjectiveType.getTypeFromInt(nbt.getInteger(nbt.getString("type")));
        switch (type) {
            case KILL_TYPE: return new ObjectiveKillType(checkpoint, type, nbt);
            case KILL_SPECIFIC: break;
        }
        return null;
    }
}
