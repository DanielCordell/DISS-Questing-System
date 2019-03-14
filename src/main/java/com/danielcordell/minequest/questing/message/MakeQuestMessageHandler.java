package com.danielcordell.minequest.questing.message;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.generators.QuestGeneratorPreviousCheckpoint;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MakeQuestMessageHandler implements IMessageHandler<MakeQuestMessage, IMessage> {
    @Override
    public IMessage onMessage(MakeQuestMessage message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        PlayerQuestData pqd = player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null);
        Quest quest = QuestGeneratorPreviousCheckpoint.generate((WorldServer) player.world, (EntityPlayerMP) player);
        pqd.startQuest(player, quest);
        return null;
    }
}