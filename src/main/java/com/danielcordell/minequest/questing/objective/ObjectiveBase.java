package com.danielcordell.minequest.questing.objective;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class ObjectiveBase {
    //Parent objects
    protected QuestCheckpoint checkpoint;
    protected Quest quest;

    //Objective Data
    protected String description;
    protected QuestState state;
    protected ObjectiveType type;

    public ObjectiveBase(QuestCheckpoint checkpoint, String description, QuestState state, ObjectiveType type) {
        this.checkpoint = checkpoint;
        this.quest = checkpoint.getQuest();
        this.description = description;
        this.state = state;
        this.type = type;
    }

    public ObjectiveBase(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        this(checkpoint, nbt.getString("description"), QuestState.getStateFromInt(nbt.getInteger("state")),
                type);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("description", description);
        nbt.setInteger("state", state.stateInt);
        nbt.setInteger("type", type.objectiveInt);
        return objectiveSpecificToNBT(nbt);
    }

    protected abstract NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt);

    public abstract void objectiveSpecificFromNBT(NBTTagCompound nbt);

    public QuestCheckpoint getCheckpoint() {
        return checkpoint;
    }

    public Quest getQuest() {
        return quest;
    }

    public String getDescription() {
        return description;
    }

    public ObjectiveType getType() {
        return type;
    }

    public QuestState getState() {
        return state;
    }

    public abstract void update(Event baseEvent);

    public abstract String getSPObjectiveInfo(EntityPlayerSP player);

    public abstract ObjectiveParamsBase getParams();

    protected void completeObjective(World world) {
        state = QuestState.COMPLETED;
        EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(quest.getPlayerID());
        if (player == null) return;
        SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("Objective Complete").setStyle(new Style().setColor(TextFormatting.WHITE)));
        player.connection.sendPacket(packet);
        packet = new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(description).setStyle(new Style().setColor(TextFormatting.GRAY).setItalic(true)));
        player.connection.sendPacket(packet);
        player.sendMessage(new TextComponentString("Quest log updated, press R to view."));
        quest.setDirty();
    }
}
