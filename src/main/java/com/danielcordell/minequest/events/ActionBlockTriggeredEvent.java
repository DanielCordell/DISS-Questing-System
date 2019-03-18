package com.danielcordell.minequest.events;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;


public class ActionBlockTriggeredEvent extends Event {
    public UUID actionBlockID;
    public World world;

    public ActionBlockTriggeredEvent(World world, UUID actionBlockID) {
        this.actionBlockID = actionBlockID;
        this.world = world;
    }
}
