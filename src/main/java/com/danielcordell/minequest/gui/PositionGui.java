package com.danielcordell.minequest.gui;

import com.danielcordell.minequest.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;


public class PositionGui extends Gui {
    public PositionGui(Minecraft mc) {
        EntityPlayerSP player = mc.player;
        BlockPos pos = player.getPosition();
        drawString(mc.fontRenderer, "Position: (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")", 4, 4, Integer.parseInt("FFFFFF", 16));
        drawString(mc.fontRenderer, "Facing: " + Util.getFacing(mc.player.getHorizontalFacing()), 4, 16, Integer.parseInt("FFFFFF", 16));
    }
}
