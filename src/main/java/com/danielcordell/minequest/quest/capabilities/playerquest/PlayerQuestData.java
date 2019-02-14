package com.danielcordell.minequest.quest.capabilities.playerquest;

import com.danielcordell.minequest.quest.Quest;
import com.danielcordell.minequest.quest.QuestBuilder;
import com.sun.istack.internal.NotNull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerQuestData {
    // Temp public
    ArrayList<Quest> playerQuests = new ArrayList<>();

    public void startQuest(EntityPlayer player, Quest quest) {
        Quest newQuest = QuestBuilder.fromNBT(quest.toNBT());
        quest.setPlayer(player);
    }

    public int numberOfQuests() {
        return playerQuests.size();
    }

    public List<Quest> getImmutableQuests() {
        return Collections.unmodifiableList(playerQuests);
    }

    public void addQuest(Quest quest) {
        playerQuests.add(quest);
    }

    public void removeQuest(int questID) {
        playerQuests.removeIf(quest -> quest.getQuestID() == questID);
    }
}
