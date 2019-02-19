package com.danielcordell.minequest;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class Util {
    public static Class<? extends EntityLivingBase> getEntityFromName(String name) {
        return EntityList.getClassFromName(name).asSubclass(EntityLivingBase.class);
    }

    public static String getNameFromEntity(Class<? extends EntityLivingBase> entity) {
        return EntityRegistry.getEntry(entity).getName();
    }
}
