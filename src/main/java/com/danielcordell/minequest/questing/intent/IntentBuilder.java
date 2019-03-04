package com.danielcordell.minequest.questing.intent;

import com.danielcordell.minequest.questing.enums.IntentType;
import com.danielcordell.minequest.questing.intent.intents.IntentGiveItemStack;
import com.danielcordell.minequest.questing.intent.intents.IntentSetNPCFollow;
import com.danielcordell.minequest.questing.intent.intents.IntentSpawnEntity;
import com.danielcordell.minequest.questing.quest.Quest;
import net.minecraft.nbt.NBTTagCompound;

public class IntentBuilder {
    public static Intent fromNBT(Quest quest, NBTTagCompound nbt) {
        IntentType type = IntentType.getTypeFromInt(nbt.getInteger("type"));
        switch (type) {
            case SPAWN_ENTITY: return new IntentSpawnEntity(nbt, quest);
            case GIVE_ITEMSTACK: return new IntentGiveItemStack(nbt, quest);
            case SET_NPC_FOLLOW: return new IntentSetNPCFollow(nbt, quest);
        }
        return null;
    }
}
