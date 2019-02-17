package com.danielcordell.minequest.questing.capabilities.playerquest;

import com.danielcordell.minequest.MineQuest;
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
        Quest clientQuest = QuestBuilder.fromNBT(quest.toNBT());
        clientQuest.setPlayer(player);
        clientQuest.start();
        player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null).addQuest(clientQuest);
    }

    public int numberOfQuests() {
        return playerQuests.size();
    }

    public List<Quest> getImmutableQuests() {
        return Collections.unmodifiableList(playerQuests);
    }

    public void addQuest(Quest quest) {
        if (containsQuest(quest.getQuestID())) {
            MineQuest.logger.error("Attempting to add quest that already is in player data!");
            MineQuest.logger.error("Old Quest: " + playerQuests.stream().filter(q -> q.getQuestID() == quest.getQuestID()).findFirst().get().toNBT());
            MineQuest.logger.error("New Quest: " + quest.toNBT());
        }
        else playerQuests.add(quest);
    }

    public void removeQuest(int questID) {
        playerQuests.removeIf(quest -> quest.getQuestID() == questID);
    }

    public boolean containsQuest(int questID) {
        return playerQuests.stream().filter(quest -> quest.getQuestID() == questID).count() > 0;
    }
}
