package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsEscort;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ObjectiveEscort extends ObjectiveBase {
    private BlockPos pos;
    private int questEntityID;
    private String structureType;

    public ObjectiveEscort(ParamsEscort params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        questEntityID = params.questEntityID;
        structureType = params.type;

        //Random
        WorldServer world = params.world;
        String structureType = params.type;
        pos = world.getChunkProvider().getNearestStructurePos(world, structureType,
                new BlockPos(world.rand.nextInt(5000) - 2500, world.getSeaLevel(), world.rand.nextInt(5000) - 2500), true
        );
    }

    public ObjectiveEscort(QuestCheckpoint checkpoint, ObjectiveType type, NBTTagCompound nbt) {
        super(checkpoint, type, nbt);
        objectiveSpecificFromNBT(nbt);
    }

    @Override
    protected NBTTagCompound objectiveSpecificToNBT(NBTTagCompound nbt) {
        nbt.setInteger("x", pos.getX());
        nbt.setInteger("y", pos.getY());
        nbt.setInteger("z", pos.getZ());
        nbt.setInteger("questEntityID", questEntityID);
        return nbt;
    }

    @Override
    public void objectiveSpecificFromNBT(NBTTagCompound nbt) {
        pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
        questEntityID = nbt.getInteger("questEntityID");
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

        MineQuest.logger.error("Mob Position: " + target.getPosition().toString());

        BlockPos nearStructurePos = world.getChunkProvider().getNearestStructurePos(world, structureType, target.getPosition(), true);
        if (nearStructurePos == null) return;

        if (nearStructurePos.equals(pos)) {
            if (world.getChunkProvider().isInsideStructure(world, structureType, target.getPosition())) {
                completeObjective(target.world);
            }
        }
    }

    @Override
    protected void completeObjective(World world) {
        EntityNPC npc = Util.getNPCFromQuestIDOrNull(questEntityID, world, quest);
        npc.clearNPCFollow();
        super.completeObjective(world);
    }

    @Override
    public String debugInfo() {
        return "Target - Take NPC to Position: " + pos.toString();
    }
}
