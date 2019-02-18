package com.danielcordell.minequest.keybind;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.playerquest.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.playerquest.PlayerQuestData;
import com.danielcordell.minequest.questing.enums.QuestState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = MineQuest.MODID)
public class KeybindEventHandler {
    @SubscribeEvent
    public static void onKeyDown(InputEvent.KeyInputEvent event) {
        if (!KeyBindings.keyBindings.get(0).isPressed()) return;
        MineQuest.logger.info("KeySide: " + FMLCommonHandler.instance().getEffectiveSide().name());
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        player.sendChatMessage("My Quests:");
        pqd.getQuests().forEach(quest -> {
            player.sendChatMessage(quest.getName() + " - Status: " + quest.getState().name());
            if (quest.getState() == QuestState.STARTED) {
                quest.getCurrentCheckpointObjectives().forEach(chkpnt -> player.sendChatMessage("__" + chkpnt.debugInfo()));
            }
        });
    }
}
