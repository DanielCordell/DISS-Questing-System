package com.danielcordell.minequest.questing.handler;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.intent.intents.IntentGiveItemStack;
import com.danielcordell.minequest.questing.intent.intents.IntentSetNPCFollow;
import com.danielcordell.minequest.questing.intent.intents.IntentSpawnEntity;
import com.danielcordell.minequest.questing.intent.params.PlayerRadiusPosParam;
import com.danielcordell.minequest.questing.message.QuestSyncMessage;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.*;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import com.danielcordell.minequest.tileentities.QuestActionTileEntity;
import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = MineQuest.MODID)
public class DataHandler {

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity ent = event.getObject();
        if (!(ent instanceof EntityPlayer)) return;
        event.addCapability(new ResourceLocation(MineQuest.MODID, CapPlayerQuestData.name), new CapPlayerQuestData());
    }

    @SubscribeEvent
    public static void onPlayerPickup(PlayerEvent.ItemPickupEvent event) {
        WorldQuestData data = WorldQuestData.get(event.player.world);
        Quest q = data.getQuestByID(0);
        if (q == null) MineQuest.logger.error("SERVER: WHOOPS ITS NULL BAD ABORT");
        else {
            MineQuest.logger.info("\nSERVER");
            MineQuest.logger.info("WorldQuests: ");
            MineQuest.logger.info(q.toNBT().toString());
            MineQuest.logger.info("PlayerDataQuests: ");
            MineQuest.logger.info(event.player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null).numberOfQuests());
        }
    }

    @SubscribeEvent
    public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        if (MineQuest.isClient(event.player.world.isRemote)) return;
        if (event.phase == TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        World world = player.world;
        if (world.getWorldTime() % 50 == 0) {
            WorldQuestData wqd = WorldQuestData.get(world);
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            //Should start quest.
            BlockPos position = player.getPosition().add(0, -1, 0);
            if (world.getBlockState(position) == ModBlocks.questStartBlock.getDefaultState()) {
                QuestStartTileEntity te = (QuestStartTileEntity) world.getTileEntity(position);
                wqd.getImmutableQuests().stream().filter(quest -> quest.getQuestID() == te.getQuestID()).forEach(quest -> {
                    if (!pqd.containsQuest(quest.getQuestID())) {
                        MineQuest.logger.info("Started New Quest!");
                        MineQuest.logger.info("Quest: " + quest.toNBT());
                        player.sendMessage(new TextComponentString("New Quest Started: " + quest.getName()));
                        pqd.startQuest(player, quest);
                    }
                });
            }
            //Check for Quest Completion
            //FAILING ON SAVE AND RELOAD? TODO CHECK THIS
            pqd.getQuests().forEach(quest -> quest.update(world));
            pqd.getQuests()
                    .stream()
                    .filter(Quest::isDirty)
                    .forEach(quest -> {
                        MineQuest.networkWrapper.sendTo(new QuestSyncMessage(quest, QuestSyncMessage.TypeOfSync.PLAYER), ((EntityPlayerMP) player));
                        quest.setClean();
                    });
        }
    }

    @SubscribeEvent
    public static void onWorldUpdate(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (world.getWorldTime() % 50 == 0) {
            WorldQuestData wqd = WorldQuestData.get(world);
            wqd.getImmutableQuests()
                    .stream()
                    .filter(Quest::isDirty)
                    .forEach(quest -> {
                        MineQuest.networkWrapper.sendToAll(new QuestSyncMessage(quest, QuestSyncMessage.TypeOfSync.WORLD));
                        quest.setClean();
                    });
        }
    }

    @SubscribeEvent
    public static void onWorldSpawn(PopulateChunkEvent.Post event) {
        if (event.getChunkX() == 0 && event.getChunkZ() == 0) {
            BlockPos pos = new BlockPos(0, event.getWorld().getSeaLevel(), 0);
            event.getWorld().setBlockState(pos, ModBlocks.questStartBlock.getDefaultState());
            ((QuestStartTileEntity) event.getWorld().getTileEntity(pos)).setQuestID(0);

            event.getWorld().setBlockState(pos.add(0, 5, 0), ModBlocks.questStartBlock.getDefaultState());
            ((QuestStartTileEntity) event.getWorld().getTileEntity(pos.add(0, 5, 0))).setQuestID(1);

            event.getWorld().setBlockState(pos.add(0, 5, 2), ModBlocks.questActionBlock.getDefaultState());
            ((QuestActionTileEntity) event.getWorld().getTileEntity(pos.add(0, 5, 2))).setActionBlockID(0);
            generateTempQuests(event);
        }
    }

    public static void generateTempQuests(PopulateChunkEvent.Post event) {
        WorldServer world = (WorldServer) event.getWorld();
        WorldQuestData data = WorldQuestData.get(world);

        //Temp todo
        if (data.getQuestByID(0) == null) {
            Quest quest = Quest.newEmptyQuest(world, "Reclaim the Land");
            //Checkpoint 1
            //Objective 1, kill 5 zombies
            QuestCheckpoint checkpoint = new QuestCheckpoint(quest);
            ObjectiveParamsBase params = new ParamsKillType(checkpoint, "Kill 2 Zombies").setParamDetails(EntityZombie.class, 2);
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.KILL_TYPE));
            //Objective 2, kill 3 entities with the tag.
            params = new ParamsKillSpecific(checkpoint, "Kill 3 Special Mobs").setParamDetails(quest.getName(), 3);
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.KILL_SPECIFIC));
            Intent intent = new IntentSpawnEntity(quest, EntitySpider.class, 3, new PlayerRadiusPosParam(10), true, quest.getName(), "TestEntity");
            checkpoint.addIntent(intent);
            quest.addCheckpoint(checkpoint);

            //Checkpoint 2
            checkpoint = new QuestCheckpoint(quest);
            params = new ParamsGather(checkpoint, "Gather 3 Items", false).setParamDetails(new ItemStack(Items.STRING), 3);
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.GATHER));

            params = new ParamsSearch(checkpoint, "Search the world!", false).setParamDetails("Village");
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.SEARCH));
            quest.addCheckpoint(checkpoint);

            quest.addFinishIntent(new IntentGiveItemStack(quest, new ItemStack(Items.DIAMOND, 3)));
            quest.addFinishIntent(new IntentGiveItemStack(quest, new ItemStack(Blocks.PURPUR_BLOCK, 32)));
            data.addQuest(quest);
            data.markDirty();
        }
        if (data.getQuestByID(1) == null) {
            EntityNPC npc = new EntityNPC(world);
            npc.setPosition(5, world.getSeaLevel() + 1, 5);
            world.spawnEntity(npc);

            Quest quest = Quest.newEmptyQuest(world, "Do stuff");
            QuestCheckpoint checkpoint = new QuestCheckpoint(quest);
            ObjectiveParamsBase params = new ParamsTrigger(checkpoint, "Interact with the block").setParamDetails(0);
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.TRIGGER));

            int entityID = quest.addEntity(npc.getUniqueID());
            params = new ParamsDeliver(checkpoint, "Give the boy some stuff").setParamDetails(new ItemStack(Items.STRING), 3, 0);
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.DELIVER));
            String type = "Village";
            params = new ParamsEscort(checkpoint, "Take me places!").setParamDetails(entityID, world, type);
            checkpoint.addObjective(ObjectiveBuilder.fromParams(params, ObjectiveType.ESCORT));
            checkpoint.addIntent(new IntentSetNPCFollow(quest, entityID));
            quest.addCheckpoint(checkpoint);
            quest.addFinishIntent(new IntentGiveItemStack(quest, new ItemStack(Items.APPLE, 4)));
            data.addQuest(quest);
            data.markDirty();
        }
    }
}
