package com.danielcordell.minequest.questing.message;

import com.danielcordell.minequest.questing.quest.QuestBuilder;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class QuestSyncMessageHandler implements IMessageHandler<QuestSyncMessage, IMessage> {
    @Override
    public IMessage onMessage(QuestSyncMessage message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (message.typeOfSync == QuestSyncMessage.TypeOfSync.PLAYER) {
                PlayerQuestData pqd = Minecraft.getMinecraft().player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
                pqd.getQuests().stream()
                        .filter(quest -> quest.getQuestID() == message.questID)
                        .findFirst()
                        .ifPresent(quest -> pqd.removeQuest(quest.getQuestID()));
                pqd.addQuest(QuestBuilder.fromNBT(message.questNBT));
            } else if (message.typeOfSync == QuestSyncMessage.TypeOfSync.WORLD) {
                WorldQuestData wqd = WorldQuestData.get(Minecraft.getMinecraft().world);
                wqd.getImmutableQuests().stream()
                        .filter(quest -> quest.getQuestID() == message.questID)
                        .findFirst()
                        .ifPresent(quest -> wqd.removeQuest(quest.getQuestID()));
                wqd.addQuest(QuestBuilder.fromNBT(message.questNBT));
            }
        });
        return null;
    }
}