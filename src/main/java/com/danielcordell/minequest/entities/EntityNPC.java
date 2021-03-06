package com.danielcordell.minequest.entities;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.google.common.base.Optional;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpectralArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
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
        this.dataManager.register(NPC_TYPE, worldIn.rand.nextInt(4));
        this.dataManager.register(NPC_FOLLOW, Optional.absent());
        this.addPotionEffect(new PotionEffect(MobEffects.GLOWING, Integer.MAX_VALUE));
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    public int getNPCType() {
        return this.dataManager.get(NPC_TYPE);
    }

    public void setNPCFollow(UUID player) {
        this.dataManager.set(NPC_FOLLOW, Optional.fromNullable(player));
        this.dataManager.setDirty(NPC_FOLLOW);
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
        this.tasks.addTask(1, new EntityAIMoveTowardsPlayer(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(10, new EntityAIOpenDoor(this, true));
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
