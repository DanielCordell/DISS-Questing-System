package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsSearch;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;


public class ObjectiveSearch extends ObjectiveBase {
    private String structureType;

    public ObjectiveSearch(ParamsSearch params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        structureType = params.structureType;
    }

    public ObjectiveSearch(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setString("structureType", structureType);
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        structureType = nbt.getString("structureType");
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof PlayerTickEvent)) return;
        if (state != QuestState.STARTED) return;
        PlayerTickEvent event = (PlayerTickEvent) baseEvent;
        EntityPlayer player = event.player;
        WorldServer world = (WorldServer) player.world;

        if (world.getChunkProvider().isInsideStructure(world, structureType, player.getPosition())) {
            completeObjective(world);
        }
    }

    @Override
    public String debugInfo() {
        return "Target - Find a " + structureType;
    }
}
