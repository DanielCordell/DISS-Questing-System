package com.danielcordell.minequest.keybind;

import com.danielcordell.minequest.MineQuest;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = MineQuest.MODID)
public class KeybindEventHandler {
    @SubscribeEvent
    public static void onKeyDown(InputEvent.KeyInputEvent event) {
        if (!KeyBindings.keyBindings.get(0).isPressed()) return;
        MineQuest.logger.info("KeySide: " + FMLCommonHandler.instance().getEffectiveSide().name());
    }
}
