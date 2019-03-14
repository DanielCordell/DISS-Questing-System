package com.danielcordell.minequest.questing.message;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.generators.QuestGeneratorPreviousCheckpoint;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClearQuestMessageHandler implements IMessageHandler<ClearQuestMessage, IMessage> {
    @Override
    public IMessage onMessage(ClearQuestMessage message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        pqd.getQuests().forEach(quest -> pqd.removeQuest(quest.getQuestID()));
        MineQuest.logger.info("Removed all quests from player " + player.getName());
        return null;
    }
}