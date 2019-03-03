package com.danielcordell.minequest.questing.handler;


import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.events.ActionBlockTriggeredEvent;
import com.danielcordell.minequest.questing.capabilities.playerquest.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.playerquest.PlayerQuestData;
import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = MineQuest.MODID)
public class QuestTriggerHandler {
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (MineQuest.isClient(event.getEntity().world.isRemote)) return;
        Entity trueSource = event.getSource().getTrueSource();
        if (!(trueSource instanceof EntityPlayer)) return;
        EntityPlayer player = ((EntityPlayer) trueSource);
        PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        if (pqd == null) return;
        pqd.updateAllCurrentObjectives(event);
    }

    @SubscribeEvent
    public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        if (MineQuest.isClient(event.player.world.isRemote)) return;
        EntityPlayer player = event.player;
        World world = player.world;
        if (world.getWorldTime() % 50 == 0) {
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            if (pqd == null) return;
            pqd.updateAllCurrentObjectives(event);
        }
    }

    @SubscribeEvent
    public static void onActionBlockTriggered(ActionBlockTriggeredEvent event) {
        if (MineQuest.isClient(event.world.isRemote)) return;
        event.world.playerEntities.forEach(player -> {
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            if (pqd == null) return;
            pqd.updateAllCurrentObjectives(event);
            player.sendMessage(new TextComponentString("Action Block Triggered!"));
        });
    }

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (MineQuest.isClient(event.getWorld().isRemote)) return;
        PlayerQuestData pqd = event.getEntityPlayer().getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        if (pqd == null) return;
        pqd.updateAllCurrentObjectives(event);
    }
}
