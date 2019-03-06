package com.danielcordell.minequest.questing.objective.objectives;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.params.ParamsDeliver;
import com.danielcordell.minequest.questing.objective.params.ParamsEscort;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.List;
import java.util.RandomAccess;


public class ObjectiveEscort extends ObjectiveBase {
    private BlockPos pos;
    private int questEntityID;

    public ObjectiveEscort(ParamsEscort params, ObjectiveType type) {
        super(params.checkpoint, params.description, params.state, params.optional, type);
        questEntityID = params.questEntityID;
        pos = params.pos;
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
        // If entity target is not san NPC stop.
        if (!(target instanceof EntityNPC)) return;
        // If this objective is not for this NPC then stop.
        if (quest.getQuestEntityIDFromEntityID(target.getUniqueID()) != questEntityID) return;

        MineQuest.logger.error("Mob Position: " + target.getPosition().toString());
        if (pos.getDistance((int) target.posX, (int) target.posY, (int) target.posZ) < 15) {
            completeObjective(target.world);
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
