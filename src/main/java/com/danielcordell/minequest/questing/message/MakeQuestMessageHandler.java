package com.danielcordell.minequest.questing.message;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.generators.QuestGenerator;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.Collection;

public class MakeQuestMessageHandler implements IMessageHandler<MakeQuestMessage, IMessage> {

    public static ArrayList<String> objectiveTypes = new ArrayList<>();

    @Override
    public IMessage onMessage(MakeQuestMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        Quest quest = QuestGenerator.generate((WorldServer) player.world, player);
        pqd.startQuest(player, quest);
        MineQuest.logger.info("Generated quest for player " + player.getName());
        return null;
    }
}