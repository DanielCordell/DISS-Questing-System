package com.danielcordell.minequest.quest.handlers;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.quest.Quest;
import com.danielcordell.minequest.quest.QuestBuilder;
import com.danielcordell.minequest.quest.QuestCheckpoint;
import com.danielcordell.minequest.quest.capabilities.playerquest.CapPlayerQuestData;
import com.danielcordell.minequest.quest.capabilities.playerquest.PlayerQuestData;
import com.danielcordell.minequest.quest.message.QuestSyncMessage;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
        EntityPlayer player = event.player;
        if (player.world.getWorldTime() % 100 == 0) {
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            pqd.getImmutableQuests()
                    .stream()
                    .filter(Quest::isDirty)
                    .forEach(quest -> MineQuest.networkWrapper.sendTo(new QuestSyncMessage(quest), ((EntityPlayerMP) player)));
        }
    }

    public static void onWorldUpdate(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (world.getWorldTime() % 100 == 0) {
            WorldQuestData wqd = WorldQuestData.get(world);
            wqd.getImmutableQuests()
                    .stream()
                    .filter(Quest::isDirty)
                    .forEach(quest -> MineQuest.networkWrapper.sendToAll(new QuestSyncMessage(quest)));
        }
    }
}
