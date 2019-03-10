package com.danielcordell.minequest.core;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.entities.RenderNPC;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntities {
    static int id = 0;

    public static void init() {
        // Every entity in our mod has an ID (local to this mod)
        EntityRegistry.registerModEntity(EntityNPC.location, EntityNPC.class, EntityNPC.location.getResourcePath(), id++, MineQuest.instance, 64, 3, true, 0x0000FF, 0xFF0000);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityNPC.class, RenderNPC::new);
    }
}
