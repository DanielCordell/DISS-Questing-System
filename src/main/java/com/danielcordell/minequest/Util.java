package com.danielcordell.minequest;

import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Util {
    public static UUID emptyUUID = new UUID(0, 0);

    public static Class<? extends EntityLivingBase> getEntityFromName(String name) {
        return EntityList.getClassFromName(name).asSubclass(EntityLivingBase.class);
    }

    public static String getNameFromEntity(Class<? extends EntityLivingBase> entity) {
        return EntityRegistry.getEntry(entity).getName();
    }

    public static boolean isEmptyUUID(UUID uuid) {
        return uuid == null || uuid.compareTo(emptyUUID) == 0;
    }

    public static EntityNPC getNPCFromQuestIDOrNull(int id, World world, Quest quest) {
        return world.getEntities(EntityNPC.class, ent -> ent.getUniqueID()
                .compareTo(quest.getEntityIDFromQuestEntityID(id)) == 0)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public static List<String> getStructuresFromDimension(int dim) {
        if (dim == DimensionType.OVERWORLD.getId())
            return Arrays.asList("Stronghold", "Mansion", "Monument", "Village", "Mineshaft", "Temple");
        if (dim == DimensionType.NETHER.getId()) return Arrays.asList("Fortress");
        if (dim == DimensionType.THE_END.getId()) return Arrays.asList("EndCity");
        return new ArrayList<>();

    }
}
