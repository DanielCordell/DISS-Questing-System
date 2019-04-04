package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ParamsDeliver extends ObjectiveParamsBase {
    public ItemStack item;
    public int count;
    public int questEntityID;
    public BlockPos nearby;

    public ParamsDeliver(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsDeliver(QuestCheckpoint checkpoint, String description, QuestState state) {
        super(checkpoint, description);
    }

    public ParamsDeliver setParamDetails(ItemStack item, int count, int questEntityID, BlockPos nearby) {
        this.item = item;
        this.count = count;
        this.questEntityID = questEntityID;
        this.nearby = nearby;
        return this;
    }
}
