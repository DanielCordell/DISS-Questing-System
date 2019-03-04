package com.danielcordell.minequest.entities;

import com.danielcordell.minequest.MineQuest;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;


public class EntityAIMoveTowardsPlayer extends EntityAIBase
{
    private final EntityNPC npc;
    private EntityPlayer player;

    public EntityAIMoveTowardsPlayer(EntityNPC creatureIn)
    {
        this.npc = creatureIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!npc.hasNPCFollow()) return false;

        UUID playerID = npc.getNPCFollow();
        if (playerID == null) return false;

        EntityPlayer player = npc.world.getPlayerEntityByUUID(playerID);
        if (player == null) {
            npc.clearNPCFollow();
            return false;
        }
        this.player = player;
        return isDistanceFromPlayer();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.npc.getNavigator().noPath() && npc.hasNPCFollow() && isDistanceFromPlayer();
    }

    private boolean isDistanceFromPlayer() {
        return player.getPositionVector().distanceTo(npc.getPositionVector()) > 5;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        //Todo with player speed
        this.npc.getNavigator().tryMoveToEntityLiving(player, 0.46);
    }
}
