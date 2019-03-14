package com.danielcordell.minequest.entities;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.google.common.base.Optional;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;


public class EntityNPC extends EntityMob {
    public static ResourceLocation location = new ResourceLocation(MineQuest.MODID, "entityNPC");
    private static final DataParameter<Integer> NPC_TYPE = EntityDataManager.createKey(EntityNPC.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> NPC_FOLLOW = EntityDataManager.createKey(EntityNPC.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    public EntityNPC(World worldIn) {
        super(worldIn);
        this.setEntityInvulnerable(true);
        this.setSize(0.6f, 1.95f);
        this.dataManager.register(NPC_TYPE, new Random().nextInt(4));
        this.dataManager.register(NPC_FOLLOW, Optional.absent());
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    public void setNPCType(int type) {
        this.dataManager.set(NPC_TYPE, type);
    }

    public int getNPCType() {
        return this.dataManager.get(NPC_TYPE);
    }

    public void setNPCFollow(UUID player) {
        this.dataManager.set(NPC_FOLLOW, Optional.fromNullable(player));
    }

    public void clearNPCFollow() {
        this.dataManager.set(NPC_FOLLOW, Optional.absent());
    }

    public UUID getNPCFollow() {
        return this.dataManager.get(NPC_FOLLOW).orNull();
    }

    public boolean hasNPCFollow() {
        return this.dataManager.get(NPC_FOLLOW).isPresent();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Here we set various attributes for our mob. Like maximum health, armor, speed, ...
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35.0D);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(4, new EntityAIMoveTowardsPlayer(this));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.dataManager.set(NPC_TYPE, compound.getInteger("npcType"));

        UUID follow = compound.getUniqueId("npcFollow");
        if (follow.compareTo(Util.emptyUUID) != 0) {
            setNPCFollow(follow);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("npcType", this.getNPCType());
        if (this.hasNPCFollow()) compound.setUniqueId("npcFollow", this.getNPCFollow());
    }
}
