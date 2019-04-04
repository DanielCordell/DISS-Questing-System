package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class ParamsEscort extends ObjectiveParamsBase {
    public WorldServer world;
    public int questEntityID;
    public String type;
    public BlockPos nearby;

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsEscort(QuestCheckpoint checkpoint, String description, QuestState state) {
        super(checkpoint, description, state);
    }

    public ParamsEscort setParamDetails(int questEntityID, WorldServer world, String type, BlockPos nearby) {
        this.world = world;
        this.questEntityID = questEntityID;
        this.type = type;
        this.nearby = nearby;
        return this;
    }

}
