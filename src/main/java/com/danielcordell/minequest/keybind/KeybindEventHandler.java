package com.danielcordell.minequest.keybind;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.message.ClearQuestMessage;
import com.danielcordell.minequest.questing.message.MakeQuestMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
        if (KeyBindings.keyBindings.get(0).isPressed()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            player.sendChatMessage("My Quests:");
            pqd.getQuests().forEach(quest -> {
                player.sendChatMessage(quest.getName() + " - Status: " + quest.getState().name());
                if (quest.getState() == QuestState.STARTED) {
                    quest.getCurrentCheckpointObjectives().forEach(chkpnt -> player.sendChatMessage("__" + chkpnt.getSPObjectiveInfo(Minecraft.getMinecraft().player)));
                }
            });
        }
        if (KeyBindings.keyBindings.get(1).isPressed()) {
            MineQuest.networkWrapper.sendToServer(new MakeQuestMessage());
        }
        if (KeyBindings.keyBindings.get(2).isPressed()) {
            MineQuest.networkWrapper.sendToServer(new ClearQuestMessage());
            PlayerQuestData pqd = Minecraft.getMinecraft().player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            pqd.getQuests().forEach(quest -> pqd.removeQuest(quest.getQuestID()));
        }
    }
}
