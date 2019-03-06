package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;

public class ParamsEscort extends ObjectiveParamsBase {
    public BlockPos pos;
    public int questEntityID;

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        super(questCheckpoint, description, optional);
    }

    public ParamsEscort(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        super(questCheckpoint, description, optional, state);
    }

    public ParamsEscort setParamDetails(int questEntityID, BlockPos pos){
        this.pos = pos;
        this.questEntityID = questEntityID;
        return this;
    }

}
