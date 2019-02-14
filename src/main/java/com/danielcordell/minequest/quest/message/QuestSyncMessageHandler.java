package com.danielcordell.minequest.quest.message;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.quest.Quest;
import com.danielcordell.minequest.quest.QuestBuilder;
import com.danielcordell.minequest.quest.capabilities.playerquest.CapPlayerQuestData;
import com.danielcordell.minequest.quest.capabilities.playerquest.PlayerQuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Optional;

public class QuestSyncMessageHandler implements IMessageHandler<QuestSyncMessage, IMessage> {
    @Override
    public IMessage onMessage(QuestSyncMessage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            PlayerQuestData pqd = Minecraft.getMinecraft().player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
            pqd.getImmutableQuests().stream().
                    filter(quest -> quest.getQuestID() == message.questID).
                    findFirst().
                    ifPresent(quest -> pqd.removeQuest(quest.getQuestID()));
            pqd.addQuest(QuestBuilder.fromNBT(message.questNBT));
        });
        return null;
    }
}