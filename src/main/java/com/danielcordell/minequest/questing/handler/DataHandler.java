package com.danielcordell.minequest.questing.handler;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.blocks.QuestStartBlock;
import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import com.danielcordell.minequest.questing.QuestCheckpoint;
import com.danielcordell.minequest.questing.capabilities.playerquest.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.playerquest.PlayerQuestData;
import com.danielcordell.minequest.questing.message.QuestSyncMessage;
import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = MineQuest.MODID)
public class DataHandler {

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        WorldQuestData data = WorldQuestData.get(event.getWorld());

        //Temp
        if (data.getQuestByID(0) == null) {
            Quest quest = new QuestBuilder(data.getFreshQuestID(), "Reclaim the Land").build();
            quest.addCheckpoint(new QuestCheckpoint(quest));
            data.addQuest(quest);
        }
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity ent = event.getObject();
        if (!(ent instanceof EntityPlayer)) return;
        event.addCapability(new ResourceLocation(MineQuest.MODID, CapPlayerQuestData.name), new CapPlayerQuestData());
    }

    @SubscribeEvent
    public static void onPlayerPickup(PlayerEvent.ItemPickupEvent event) {
        //if (MineQuest.isClient(event.player.world.isRemote)) return;
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
        EntityPlayer player = event.player;
        World world = player.world;
        if (world.getWorldTime() % 100 == 0) {
            WorldQuestData wqd = WorldQuestData.get(world);
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            BlockPos position = player.getPosition().add(0, -1, 0);
            MineQuest.logger.error(world.getBlockState(position).getBlock().getRegistryName());
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
            pqd.getImmutableQuests()
                    .stream()
                    .filter(Quest::isDirty)
                    .forEach(quest -> MineQuest.networkWrapper.sendTo(new QuestSyncMessage(quest, QuestSyncMessage.TypeOfSync.PLAYER), ((EntityPlayerMP) player)));
        }
    }

    @SubscribeEvent
    public static void onWorldUpdate(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (world.getWorldTime() % 100 == 0) {
            WorldQuestData wqd = WorldQuestData.get(world);
            wqd.getImmutableQuests()
                    .stream()
                    .filter(Quest::isDirty)
                    .forEach(quest -> MineQuest.networkWrapper.sendToAll(new QuestSyncMessage(quest, QuestSyncMessage.TypeOfSync.WORLD)));
        }
    }

    @SubscribeEvent
    public static void onWorldSpawn(PopulateChunkEvent.Post event) {
        if (event.getChunkX() == 0 && event.getChunkZ() == 0) {
            BlockPos pos = new BlockPos(0, event.getWorld().getSeaLevel(), 0);
            event.getWorld().setBlockState(pos, ModBlocks.questStartBlock.getDefaultState());
            ((QuestStartTileEntity) event.getWorld().getTileEntity(pos)).setQuestID(0);
        }
    }
}
