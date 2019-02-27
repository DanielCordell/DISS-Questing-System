package com.danielcordell.minequest.questing.intent.intents;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.intent.params.PosParamBase;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;

public class IntentSpawnEntity extends Intent {
    private final boolean persistent;
    private final String nametag;
    private Class<? extends EntityLivingBase> entityToSpawn;
    private int numToSpawn;
    private String entityData;
    private PosParamBase posParam;

    public IntentSpawnEntity(Quest quest, Class<? extends EntityLivingBase> entityToSpawn, int numToSpawn, PosParamBase posParam){
        this(quest, entityToSpawn, numToSpawn, posParam, false, null, null);
    }
    public IntentSpawnEntity(Quest quest, Class<? extends EntityLivingBase> entityToSpawn, int numToSpawn, PosParamBase posParam, boolean persistent, String entityData, String nametag) {
        super(quest);
        this.entityToSpawn = entityToSpawn;
        this.numToSpawn = numToSpawn;
        this.posParam = posParam;
        this.entityData = entityData;
        this.persistent = persistent;
        this.nametag = nametag;
    }

    public IntentSpawnEntity(NBTTagCompound nbt, Quest quest) {
        this(quest,
                Util.getEntityFromName(nbt.getString("entityToSpawn")),
                nbt.getInteger("numToSpawn"),
                PosParamBase.fromNBT((NBTTagCompound) nbt.getTag("posParam")),
                nbt.getBoolean("persistent"),
                nbt.hasKey("entityData") ? nbt.getString("entityData") : null,
                nbt.hasKey("nametag") ? nbt.getString("nametag") : null
        );
    }

    @Override
    public void perform(World world) {
        try {
            for (int i = 0; i < numToSpawn; ++i) {
                EntityLiving entity = (EntityLiving) entityToSpawn.getConstructors()[0].newInstance(world);
                BlockPos pos = posParam.getPos(world, quest);
                entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
                if (entityData != null) entity.getEntityData().setBoolean(entityData, true);
                if (nametag != null) entity.setCustomNameTag(nametag);
                if (persistent) entity.enablePersistence();
                world.spawnEntity(entity);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            MineQuest.logger.error(e);
            MineQuest.logger.error("Could not spawn entity/entities.");
        }
    }

    @Override
    public NBTTagCompound toIntentSpecificNBT(NBTTagCompound nbt) {
        nbt.setString("entityToSpawn", Util.getNameFromEntity(entityToSpawn));
        nbt.setInteger("numToSpawn", numToSpawn);
        nbt.setTag("posParam", posParam.toNBT());
        nbt.setBoolean("persistent", persistent);
        if (entityData != null) nbt.setString("entityData", entityData);
        if (nametag != null) nbt.setString("nametag", nametag);
        return nbt;
    }

    @Override
    public IntentType getIntentType() {
        return IntentType.SPAWN_ENTITY;
    }
}
