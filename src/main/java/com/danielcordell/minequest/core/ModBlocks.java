package com.danielcordell.minequest.core;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.blocks.QuestStartBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(MineQuest.MODID)
public class ModBlocks {
    public static QuestStartBlock questStartBlock = new QuestStartBlock();
}
