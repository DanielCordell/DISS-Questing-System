package com.danielcordell.minequest.questing.objective.params;

import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

public class ParamsDeliver extends ObjectiveParamsBase {
    public WorldServer world;
    public ItemStack item;
    public int count;
    public int questEntityID;

    public ParamsDeliver(QuestCheckpoint questCheckpoint, String description) {
        super(questCheckpoint, description);
    }

    public ParamsDeliver(QuestCheckpoint questCheckpoint, String description, boolean optional) {
        super(questCheckpoint, description, optional);
    }

    public ParamsDeliver(QuestCheckpoint questCheckpoint, String description, boolean optional, QuestState state) {
        super(questCheckpoint, description, optional, state);
    }

    public ParamsDeliver setParamDetails(ItemStack item, int count, int questEntityID, WorldServer world) {
        this.item = item;
        this.count = count;
        this.questEntityID = questEntityID;
        this.world = world;
        return this;
    }
}
