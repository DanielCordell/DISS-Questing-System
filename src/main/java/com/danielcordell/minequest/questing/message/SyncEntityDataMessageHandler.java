package com.danielcordell.minequest.questing.message;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.capabilities.CapPlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class SyncEntityDataMessageHandler implements IMessageHandler<SyncEntityDataMessage, IMessage> {
    @Override
    public IMessage onMessage(SyncEntityDataMessage message, MessageContext ctx) {
        Entity ent = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
        if (ent == null) {
            MineQuest.logger.error("Entity to sync does not exist!");
        }
        else {
            ent.getEntityData().setString("inQuest", message.entityData);
        }
        //Todo do this with acknowledgement packets
        return null;
    }
}