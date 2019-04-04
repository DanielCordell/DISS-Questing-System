package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.item.ItemStack;

public class ParamsGather extends ObjectiveParamsBase {
    public ItemStack item;
    public int count;

    public ParamsGather(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsGather(QuestCheckpoint checkpoint, String description, QuestState state) {
        super(checkpoint, description, state);
    }

    public ParamsGather setParamDetails(ItemStack item, int count) {
        this.item = item;
        this.count = count;
        return this;
    }
}
