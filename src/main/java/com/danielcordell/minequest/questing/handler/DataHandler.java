package com.danielcordell.minequest.questing.handler;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.generators.QuestGeneratorPreviousCheckpoint;
import com.danielcordell.minequest.questing.message.QuestSyncMessage;
import com.danielcordell.minequest.questing.message.SyncEntityDataMessage;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        WorldServer world = (WorldServer) player.world;
        if (world.getWorldTime() % 20 == 0) {

            if (world.getWorldTime() < 20 * 3600 * 5) {
                world.setTotalWorldTime(20 * 3600 * 5);
            }

            WorldQuestData wqd = WorldQuestData.get(world);
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            //Should start quest.
            BlockPos position = player.getPosition().add(0, -1, 0);

            //Detect if standing on a quest trigger block.
            if (world.getBlockState(position) == ModBlocks.questStartBlock.getDefaultState()) {
                QuestStartTileEntity te = (QuestStartTileEntity) world.getTileEntity(position);
                wqd.getImmutableQuests().stream().filter(quest -> quest.getQuestID() == te.getQuestID()).forEach(quest -> {
                    if (!pqd.containsQuest(quest.getQuestID())) {
                        pqd.startQuest(player, quest);
                        MineQuest.logger.info("Started New Quest!");
                        MineQuest.logger.info("Quest: " + quest.toNBT());
                        player.sendMessage(new TextComponentString("New Quest Started: " + quest.getName()));
                    }
                });
            }

            if (!pqd.canGenerate()) {
                AdvancementManager am = world.getAdvancementManager();
                Advancement pick = am.getAdvancement(new ResourceLocation("story/upgrade_tools"));
                Advancement sword = am.getAdvancement(new ResourceLocation("adventure/kill_a_mob"));
                PlayerAdvancements advancements = player.getAdvancements();
                if (advancements.getProgress(pick).isDone() && advancements.getProgress(sword).isDone()) {
                    pqd.startGenerating();
                    player.sendMessage(new TextComponentString("Quests will now begin generating."));
                }
            }

            //If it's been enough time & no more than 3 active quests then generate a new one.
            if (pqd.canGenerate() && world.getTotalWorldTime() > pqd.getTimeLastGenerated() + pqd.getTimeUntilNext() && pqd.getNumberOfActiveQuests() < 3L) {
                Quest quest = QuestGeneratorPreviousCheckpoint.generate(world, player);
                pqd.startQuest(player, quest);
                pqd.setTimeLastGenerated(event, world.getTotalWorldTime()); // Between 8 and 15 minutes
                pqd.setTimeUntilNext(event, world.rand.nextInt(9600) + 8400);
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
    public static void onMobTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase e = event.getEntityLiving();
        if (MineQuest.isClient(event.getEntity().world.isRemote)) return;
        NBTTagCompound entityData = e.getEntityData();
        if (entityData.hasKey("inQuest") && !entityData.hasKey("synced") && e.ticksExisted > 20) {
            MineQuest.networkWrapper.sendToAll(new SyncEntityDataMessage(entityData.getString("inQuest"), e.getEntityId()));
            entityData.setBoolean("synced", true);
        }
    }

    @SubscribeEvent
    public static void onMobDamage(LivingDamageEvent event) {
        if (event.getEntity().getEntityData().hasKey("inQuest")) {
            if (!event.getSource().damageType.equalsIgnoreCase("player")) {
                event.setCanceled(true);
            }
        }
    }
}
