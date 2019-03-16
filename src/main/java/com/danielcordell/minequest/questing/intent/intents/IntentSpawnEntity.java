package com.danielcordell.minequest.questing.intent.intents;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.intent.Intent;
import com.danielcordell.minequest.questing.intent.params.PosParamBase;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class IntentSpawnEntity extends Intent {
    private final String nametag;
    private Class<? extends EntityLivingBase> entityToSpawn;
    private int numToSpawn;
    private String entityData;
    private PosParamBase posParam;
    private int armorLevel;

    public IntentSpawnEntity(Quest quest, Class<? extends EntityLivingBase> entityToSpawn, int numToSpawn, PosParamBase posParam, String entityData, String nametag, int armorLevel) {
        super(quest);
        this.entityToSpawn = entityToSpawn;
        this.numToSpawn = numToSpawn;
        this.posParam = posParam;
        this.entityData = entityData;
        this.nametag = nametag;
    }

    public IntentSpawnEntity(NBTTagCompound nbt, Quest quest) {
        this(quest,
                Util.getEntityFromName(nbt.getString("entityToSpawn")),
                nbt.getInteger("numToSpawn"),
                PosParamBase.fromNBT((NBTTagCompound) nbt.getTag("posParam")),
                nbt.hasKey("entityData") ? nbt.getString("entityData") : null,
                nbt.hasKey("nametag") ? nbt.getString("nametag") : null,
                nbt.getInteger("armorLevel")
        );
    }

    @Override
    public void perform(World world) {
        try {
            for (int i = 0; i < numToSpawn; ++i) {
                EntityLiving entity = (EntityLiving) entityToSpawn.getConstructors()[0].newInstance(world);
                BlockPos pos = posParam.getPos(world, quest);
                entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
                world.spawnEntity(entity);
                entity.enablePersistence();
                if (nametag != null) entity.setCustomNameTag(nametag);
                if (entityData != null) {
                    entity.getEntityData().setString("inQuest", entityData);
                }
                entity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, Integer.MAX_VALUE));

                // Give armor

                ItemArmor head = null;
                ItemArmor chest = null;
                ItemArmor legs = null;
                ItemArmor feet = null;

                int selector = world.rand.nextInt(6) + 1;

                switch (armorLevel) {
                    case 0: //Leather
                        head = Items.LEATHER_HELMET;
                        if ((selector & 0x001) == 0x001) chest = Items.LEATHER_CHESTPLATE;
                        if ((selector & 0x010) == 0x010) legs = Items.LEATHER_LEGGINGS;
                        if ((selector & 0x100) == 0x100) feet = Items.LEATHER_BOOTS;
                        break;
                    case 1: //Chain
                        head = Items.CHAINMAIL_HELMET;
                        if ((selector & 0x001) == 0x001) chest = Items.CHAINMAIL_CHESTPLATE;
                        if ((selector & 0x010) == 0x010) legs = Items.CHAINMAIL_LEGGINGS;
                        if ((selector & 0x100) == 0x100) feet = Items.CHAINMAIL_BOOTS;
                        break;
                    case 2: //Iron
                        head = Items.IRON_HELMET;
                        if ((selector & 0x001) == 0x001) chest = Items.IRON_CHESTPLATE;
                        if ((selector & 0x010) == 0x010) legs = Items.IRON_LEGGINGS;
                        if ((selector & 0x100) == 0x100) feet = Items.IRON_BOOTS;
                        break;
                    case 4: //All Diamond
                        selector = 0x111;
                    default: //Some Diamond
                        head = Items.DIAMOND_HELMET;
                        if ((selector & 0x001) == 0x001) chest = Items.DIAMOND_CHESTPLATE;
                        if ((selector & 0x010) == 0x010) legs = Items.DIAMOND_LEGGINGS;
                        if ((selector & 0x100) == 0x100) feet = Items.DIAMOND_BOOTS;
                }
                entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(head));
                if (chest != null) entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(chest));
                if (legs != null) entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(legs));
                if (feet != null) entity.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(feet));
                if (entity instanceof EntitySkeleton) entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
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
        if (entityData != null) nbt.setString("entityData", entityData);
        if (nametag != null) nbt.setString("nametag", nametag);
        nbt.setInteger("armorLevel", armorLevel);
        return nbt;
    }

    @Override
    public IntentType getIntentType() {
        return IntentType.SPAWN_ENTITY;
    }
}
