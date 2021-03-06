package com.danielcordell.minequest.questing.handlers;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.generators.QuestGenerator;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        if (MineQuest.isClient(event.player.world.isRemote)) return;
        if (event.phase == TickEvent.Phase.END) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        WorldServer world = (WorldServer) player.world;
        if (world.getWorldTime() % 20 == 0) {
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
                        player.sendMessage(new TextComponentString("Press R ingame (not with chat open) to view your quests in the chat log."));
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
                    player.sendMessage(new TextComponentString("Press R ingame (not with chat open) to view your quests in the chat log."));
                    player.sendMessage(new TextComponentString("Hover over underlined objectives for more information."));
                }
            }

            //If it's been enough time & no more than 3 active quests then generate a new one.
            if (pqd.canGenerate() && world.getTotalWorldTime() > pqd.getTimeLastGenerated() + pqd.getTimeUntilNext() && pqd.getNumberOfActiveQuests() < 3L) {
                Quest quest = QuestGenerator.generate(world, player);
                pqd.startQuest(player, quest);
                pqd.setTimeLastGenerated(event, world.getTotalWorldTime());
                pqd.setTimeUntilNext(event, world.rand.nextInt(9600) + 8400); // Between 8 and 15 minutes
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
        if (world.getWorldTime() % 100 == 0) {
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
        onMobDamage(event, event.getSource().damageType);
    }

    @SubscribeEvent
    public static void onMobDamage(LivingHurtEvent event) {
        onMobDamage(event, event.getSource().damageType);
    }

    private static void onMobDamage(LivingEvent event, String damageType) {
        if (event.getEntity().getEntityData().hasKey("inQuest")) {
            if (damageType.equalsIgnoreCase(DamageSource.OUT_OF_WORLD.damageType) || damageType.equalsIgnoreCase(DamageSource.ANVIL.damageType) || damageType.equalsIgnoreCase(DamageSource.MAGIC.damageType))
                return;

            if (!damageType.equalsIgnoreCase("player")) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(net.minecraftforge.event.entity.player.PlayerEvent.Clone event){
        EntityPlayer original = event.getOriginal();
        if (!event.isWasDeath()) return;
        PlayerQuestData capOld = original.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        PlayerQuestData capNew = event.getEntityPlayer().getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        capOld.copyTo(capNew);
    }
}
