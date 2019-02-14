package com.danielcordell.minequest.quest.message;

import com.danielcordell.minequest.quest.Quest;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class QuestSyncMessage implements IMessage {
    public NBTTagCompound questNBT;
    public int questID;

    public QuestSyncMessage(Quest quest) {
        this.questID = quest.getQuestID();
        this.questNBT = quest.toNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        questID = buf.readInt();
        questNBT = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(questID);
        ByteBufUtils.writeTag(buf, questNBT);
    }
}
