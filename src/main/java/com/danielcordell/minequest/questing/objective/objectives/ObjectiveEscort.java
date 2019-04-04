package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.ParamsEscort;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ObjectiveEscort extends ObjectiveBase {
    private BlockPos pos;
    private int questEntityID;
    private String structureType;
    private BlockPos nearby;

    public ObjectiveEscort(ParamsEscort params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, type);
        questEntityID = params.questEntityID;
        structureType = params.type;

        WorldServer world = params.world;
        String structureType = params.type;
        pos = world.getChunkProvider().getNearestStructurePos(world, structureType,
                new BlockPos(params.nearby.getX() + world.rand.nextInt(500) - 250, world.getSeaLevel(), params.nearby.getZ() + world.rand.nextInt(500) - 250), true
        );
        nearby = params.nearby;
    }

    public ObjectiveEscort(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setTag("pos", NBTUtil.createPosTag(pos));
        nbt.setInteger("questEntityID", questEntityID);
        nbt.setString("structureType", structureType);
        nbt.setTag("nearby", NBTUtil.createPosTag(nearby));
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        questEntityID = nbt.getInteger("questEntityID");
        nearby = NBTUtil.getPosFromTag(((NBTTagCompound) nbt.getTag("nearby")));
        structureType = nbt.getString("structureType");
        pos = NBTUtil.getPosFromTag(((NBTTagCompound) nbt.getTag("pos")));
    }

    @Override
    public void update(Event baseEvent) {
        if (!(baseEvent instanceof LivingUpdateEvent)) return;
        if (state != QuestState.STARTED) return;
        LivingUpdateEvent event = (LivingUpdateEvent) baseEvent;
        EntityLivingBase target = event.getEntityLiving();
        WorldServer world = (WorldServer) target.world;

        // If entity target is not san NPC stop.
        if (!(target instanceof EntityNPC)) return;
        // If this objective is not for this NPC then stop.
        if (quest.getQuestEntityIDFromEntityID(target.getUniqueID()) != questEntityID) return;

        BlockPos nearStructurePos = world.getChunkProvider().getNearestStructurePos(world, structureType, target.getPosition(), false);

        if (nearStructurePos == null) return;

        if (nearStructurePos.equals(pos)) {
            if (world.getChunkProvider().isInsideStructure(world, structureType, target.getPosition())) {
                completeObjective(target.world);
            }
        }
    }

    @Override
    protected void completeObjective(World world) {
        super.completeObjective(world);
        EntityNPC npc = Util.getNPCFromQuestIDOrNull(questEntityID, world, quest);
        if (checkpoint.getObjectives().stream().noneMatch(it -> it instanceof ObjectiveEscort && it.getState() == QuestState.STARTED))
            npc.clearNPCFollow();
    }

    @Override
    public String getSPObjectiveInfo(EntityPlayerSP player) {
        String direction = Util.getDirectionFromPositions(player.getPosition(), pos);
        int distance = (int) Math.round(player.getPosition().getDistance(pos.getX(), pos.getY(), pos.getZ()));
        return "Take NPC from Position (" + nearby.getX() + ", " + nearby.getY() + ", " + nearby.getZ() +") to the " + structureType + " " + direction + " of here. (" + distance + "m)";
    }

    @Override
    public ObjectiveParamsBase getParams() {
        return new ParamsEscort(checkpoint, getDescription(), state).setParamDetails(questEntityID, null, structureType,  nearby);
    }

    public String getStructureType() {
        return structureType;
    }
}
