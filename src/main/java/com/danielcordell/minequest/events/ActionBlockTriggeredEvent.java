package com.danielcordell.minequest.events;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ActionBlockTriggeredEvent extends Event {
    public int actionBlockID;
    public World world;

    public ActionBlockTriggeredEvent(World world, int actionBlockID) {
        this.actionBlockID = actionBlockID;
        this.world = world;
    }
}
