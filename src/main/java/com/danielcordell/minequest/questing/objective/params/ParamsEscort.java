package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.world.WorldServer;

public class ParamsEscort extends ObjectiveParamsBase {
    public WorldServer world;
    public int questEntityID;
    public String type;

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        super(questCheckpoint, description, optional);
    }

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        super(questCheckpoint, description, optional, state);
    }

    public ParamsEscort setParamDetails(int questEntityID, WorldServer world, String type) {
        this.world = world;
        this.questEntityID = questEntityID;
        this.type = type;
        return this;
    }

}
