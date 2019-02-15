package com.danielcordell.minequest.questing.capabilities.playerquest;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;

public class CapPlayerQuestData implements ICapabilitySerializable<NBTBase> {
    public static String name = "player_quest_data";

    @CapabilityInject(PlayerQuestData.class)
    public static final Capability<PlayerQuestData> PLAYER_QUEST_DATA = null;

    private PlayerQuestData instance = PLAYER_QUEST_DATA.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == PLAYER_QUEST_DATA;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        return capability == PLAYER_QUEST_DATA ? PLAYER_QUEST_DATA.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return PLAYER_QUEST_DATA.getStorage().writeNBT(PLAYER_QUEST_DATA, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        PLAYER_QUEST_DATA.getStorage().readNBT(PLAYER_QUEST_DATA, this.instance, null, nbt);
    }

}

