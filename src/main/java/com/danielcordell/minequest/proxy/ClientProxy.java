package com.danielcordell.minequest.proxy;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.core.ModEntities;
import com.danielcordell.minequest.keybind.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModEntities.initModels();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        KeyBindings.keyBindings.forEach(ClientRegistry::registerKeyBinding);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {

    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event) {
        ModBlocks.questActionBlock.initModel();
        ModBlocks.questStartBlock.initModel();
    }

}
