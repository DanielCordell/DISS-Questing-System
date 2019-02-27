package com.danielcordell.minequest.entities;

import com.danielcordell.minequest.MineQuest;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


public class RenderNPC extends RenderLiving<EntityNPC> {
    private final ResourceLocation NPC_COOL_TEXTURE = new ResourceLocation(MineQuest.MODID, "textures/entity/npc/npc_cool.png");
    private final ResourceLocation NPC_COP_TEXTURE = new ResourceLocation(MineQuest.MODID, "textures/entity/npc/npc_cop.png");
    private final ResourceLocation NPC_MEDIC_TEXTURE = new ResourceLocation(MineQuest.MODID, "textures/entity/npc/npc_medic.png");
    private final ResourceLocation NPC_PRIEST_TEXTURE = new ResourceLocation(MineQuest.MODID, "textures/entity/npc/npc_priest.png");


    public RenderNPC(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelVillager(0), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityNPC entity) {
        switch (entity.getNPCType()) {
            case 0: return NPC_COOL_TEXTURE;
            case 1: return NPC_COP_TEXTURE;
            case 2: return NPC_MEDIC_TEXTURE;
            case 3: return NPC_COOL_TEXTURE;
            default: return NPC_PRIEST_TEXTURE;
        }
    }
}
