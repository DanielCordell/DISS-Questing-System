package com.danielcordell.minequest.questing.capabilities;

import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class StoragePlayerQuestData implements Capability.IStorage<PlayerQuestData> {

    @Override
    public NBTBase writeNBT(Capability<PlayerQuestData> capability, PlayerQuestData instance, EnumFacing side) {
        NBTTagCompound base = new NBTTagCompound();
        base.setBoolean("canGenerate", instance.canGenerate);
        base.setLong("timeLastGenerated", instance.timeLastGenerated);
        base.setInteger("timeUntilNext", instance.timeUntilNext);
        NBTTagList nbt = new NBTTagList();
        instance.playerQuests.forEach(quest -> nbt.appendTag(quest.toNBT()));
        base.setTag("list", nbt);
        return base;
    }

    @Override
    public void readNBT(Capability<PlayerQuestData> capability, PlayerQuestData instance, EnumFacing side, NBTBase nbtbase) {
        NBTTagCompound base = (NBTTagCompound) nbtbase;
        NBTTagList nbt = ((NBTTagList) base.getTag("list"));
        ArrayList<Quest> quests = new ArrayList<>();
        nbt.forEach(questNBT -> quests.add(QuestBuilder.fromNBT(((NBTTagCompound) questNBT))));
        instance.playerQuests = quests;
        instance.canGenerate = base.getBoolean("canGenerate");
        instance.timeLastGenerated = base.getLong("timeLastGenerated");
        instance.timeUntilNext = base.getInteger("timeUntilNext");
    }
}
