package com.danielcordell.minequest.worldgenerator;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.intent.intents.IntentGiveItemStack;
import com.danielcordell.minequest.questing.objective.objectives.ObjectiveTrigger;
import com.danielcordell.minequest.questing.objective.params.ParamsTrigger;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.UUID;


public class DungeonGenerator implements IWorldGenerator {

    private static final ResourceLocation PYRAMID = new ResourceLocation(MineQuest.MODID+":pyramid");

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (chunkX % 16 != 0 || chunkZ % 16 != 0) return;

        WorldQuestData wqd = WorldQuestData.get(world);
        int id = wqd.getFreshQuestID();
        Quest quest = new QuestBuilder(id, "To the heart of the dungeon!").build();
        QuestCheckpoint checkpoint = new QuestCheckpoint(quest);

        UUID actionID = UUID.randomUUID();
        ObjectiveTrigger obj = new ObjectiveTrigger(new ParamsTrigger(checkpoint, "Find the heart of the dungeon!").setParamDetails(actionID), ObjectiveType.TRIGGER);
        checkpoint.addObjective(obj);
        quest.addCheckpoint(checkpoint);
        quest.addFinishIntent(new IntentGiveItemStack(quest, new ItemStack(Blocks.GOLD_BLOCK, 2)));
        wqd.addQuest(quest);
        //tODO WAS AUTO OCMPLETING?
        DungeonTemplateProcessor processor = new DungeonTemplateProcessor(id, actionID);

        final BlockPos basePos = new BlockPos(chunkX * 16 + random.nextInt(16), world.getSeaLevel(), chunkZ * 16 + random.nextInt(16));
        final PlacementSettings settings = new PlacementSettings().setRotation(Rotation.NONE);
        final Template template = world.getSaveHandler().getStructureTemplateManager().getTemplate(world.getMinecraftServer(), PYRAMID);

        template.addBlocksToWorld(world, basePos, processor, settings, 0x01 | 0x10);
    }
}
