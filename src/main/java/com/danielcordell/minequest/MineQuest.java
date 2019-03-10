package com.danielcordell.minequest;

import com.danielcordell.minequest.core.ModBlocks;
import com.danielcordell.minequest.core.ModEntities;
import com.danielcordell.minequest.proxy.IProxy;
import com.danielcordell.minequest.questing.capabilities.PlayerQuestData;
import com.danielcordell.minequest.questing.capabilities.StoragePlayerQuestData;
import com.danielcordell.minequest.questing.message.QuestSyncMessage;
import com.danielcordell.minequest.questing.message.QuestSyncMessageHandler;
import com.danielcordell.minequest.tileentities.QuestActionTileEntity;
import com.danielcordell.minequest.tileentities.QuestStartTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = MineQuest.MODID)
@Mod(modid = MineQuest.MODID, name = MineQuest.MODNAME, version = MineQuest.MODVERSION, acceptedMinecraftVersions = "1.12.2")
public class MineQuest {
    public static final String MODID = "mine_quest";
    static final String MODNAME = "Mine Quest";
    static final String MODVERSION = "1.0";

    public static Logger logger;

    private static int id = 0;
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @Mod.Instance
    public static MineQuest instance;


    @SidedProxy(clientSide = "com.danielcordell.minequest.proxy.ClientProxy", serverSide = "com.danielcordell.minequest.proxy.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("preInit");
        ModEntities.init();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("init");
        CapabilityManager.INSTANCE.register(PlayerQuestData.class, new StoragePlayerQuestData(), PlayerQuestData::new);
        networkWrapper.registerMessage(QuestSyncMessageHandler.class, QuestSyncMessage.class, id++, Side.CLIENT);
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("postInit");
        proxy.postInit(event);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.questStartBlock, ModBlocks.questActionBlock);
        GameRegistry.registerTileEntity(QuestStartTileEntity.class, new ResourceLocation(MODID, "queststartblock"));
        GameRegistry.registerTileEntity(QuestActionTileEntity.class, new ResourceLocation(MODID, "questactionblock"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(ModBlocks.questStartBlock).setRegistryName(ModBlocks.questStartBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.questActionBlock).setRegistryName(ModBlocks.questActionBlock.getRegistryName()));
    }

    // Hate the naming of isRemote so little convenience function here for my sanity.
    public static boolean isClient(boolean isRemote) {
        return isRemote;
    }

    public static boolean isServer(boolean isRemote) {
        return !isRemote;
    }
}
