package com.danielcordell.minequest.questing.message;

import com.danielcordell.minequest.questing.quest.Quest;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class QuestSyncMessage implements IMessage {
    TypeOfSync typeOfSync;
    NBTTagCompound questNBT;
    int questID;

    public QuestSyncMessage() {
    }

    public QuestSyncMessage(Quest quest, TypeOfSync typeOfSync) {
        this.questID = quest.getQuestID();
        this.questNBT = quest.toNBT();
        this.typeOfSync = typeOfSync;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        questID = buf.readInt();
        questNBT = ByteBufUtils.readTag(buf);
        typeOfSync = TypeOfSync.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(questID);
        ByteBufUtils.writeTag(buf, questNBT);
        buf.writeInt(typeOfSync.ordinal());
    }

    public enum TypeOfSync {
        WORLD,
        PLAYER
    }
}
