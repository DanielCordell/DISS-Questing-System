package com.danielcordell.minequest.keybind;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.message.ClearQuestMessage;
import com.danielcordell.minequest.questing.message.MakeQuestMessage;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
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
            player.sendMessage(new TextComponentString("\nMy Quests:\n").setStyle(new Style().setBold(true).setColor(TextFormatting.WHITE)));
            TextFormatting alternating = TextFormatting.WHITE;
            for (Quest quest : pqd.getQuests()) {
                alternating = (alternating == TextFormatting.WHITE) ? TextFormatting.GRAY : TextFormatting.WHITE;
                Style style = new Style().setColor(alternating);
                String questStatus = quest.getName() + " - Status: " + quest.getState().name();
                player.sendMessage(new TextComponentString(questStatus).setStyle(style));
                if (quest.getState() == QuestState.STARTED) {
                    for (ObjectiveBase objective : quest.getCurrentCheckpointObjectives()) {
                        if (objective.getState() == QuestState.COMPLETED) continue;
                        String objectiveName = '\u00bb' + " " + objective.getDescription();
                        String objectiveInfo = objective.getSPObjectiveInfo(player);
                        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(objectiveInfo).setStyle(style.setUnderlined(false)));
                        player.sendMessage(new TextComponentString(objectiveName).setStyle(style.setHoverEvent(hoverEvent).setUnderlined(true)));
                    }
                }
            }
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
