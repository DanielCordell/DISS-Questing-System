package com.danielcordell.minequest.questing.capabilities.playerquest;

import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerQuestData {
    // Temp public
    ArrayList<Quest> playerQuests = new ArrayList<>();

    public void startQuest(EntityPlayer player, Quest quest) {
        quest.setPlayer(player);
        Quest clientQuest = QuestBuilder.fromNBT(quest.toNBT());
        player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null).addQuest(quest);
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
