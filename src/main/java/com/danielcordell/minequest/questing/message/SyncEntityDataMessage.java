package com.danielcordell.minequest.questing.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class SyncEntityDataMessage implements IMessage {

    public String entityData;
    public int entityID;

    public SyncEntityDataMessage(String entityData, int entityID){
        this.entityData = entityData;
        this.entityID = entityID;
    }

    public SyncEntityDataMessage() {}


    @Override
    public void fromBytes(ByteBuf buf) {
        entityData = ByteBufUtils.readUTF8String(buf);
        entityID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, entityData);
        buf.writeInt(entityID);
    }
}
