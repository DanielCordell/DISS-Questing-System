package com.danielcordell.minequest.entities;

import com.danielcordell.minequest.MineQuest;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    @Override
    public void updateTask(){
        if (this.npc.getDistanceSq(this.player) >= 144.0D)
        {
            int i = MathHelper.floor(this.player.posX) - 2;
            int j = MathHelper.floor(this.player.posZ) - 2;
            int k = MathHelper.floor(this.player.getEntityBoundingBox().minY);

            for (int l = 0; l <= 4; ++l)
            {
                for (int i1 = 0; i1 <= 4; ++i1)
                {
                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
                    {
                        npc.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), npc.rotationYaw, npc.rotationPitch);
                        npc.getNavigator().clearPath();
                        return;
                    }
                }
            }
        }

    }

    protected boolean isTeleportFriendlyBlock(int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
    {
        BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
        IBlockState iblockstate = npc.world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(npc.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(npc) && npc.world.isAirBlock(blockpos.up()) && npc.world.isAirBlock(blockpos.up(2));
    }


    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.npc.getNavigator().tryMoveToEntityLiving(player, 0.46);
    }
}
