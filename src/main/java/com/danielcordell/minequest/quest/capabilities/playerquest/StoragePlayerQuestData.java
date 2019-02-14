package com.danielcordell.minequest.quest.capabilities.playerquest;

import com.danielcordell.minequest.quest.Quest;
import com.danielcordell.minequest.quest.QuestBuilder;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class StoragePlayerQuestData implements Capability.IStorage<PlayerQuestData> {

    @Override
    public NBTBase writeNBT(Capability<PlayerQuestData> capability, PlayerQuestData instance, EnumFacing side) {
        NBTTagList nbt = new NBTTagList();
        instance.playerQuests.forEach(quest -> nbt.appendTag(quest.toNBT()));
        return nbt;
    }

    @Override
    public void readNBT(Capability<PlayerQuestData> capability, PlayerQuestData instance, EnumFacing side, NBTBase nbtbase) {
        NBTTagList nbt = ((NBTTagList) nbtbase);
        ArrayList<Quest> quests = new ArrayList<>();
        nbt.forEach(questNBT -> quests.add(QuestBuilder.fromNBT(((NBTTagCompound) questNBT))));
        instance.playerQuests = quests;
    }
}
