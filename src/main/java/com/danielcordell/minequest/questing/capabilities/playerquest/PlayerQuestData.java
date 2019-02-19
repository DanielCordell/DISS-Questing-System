package com.danielcordell.minequest.questing.capabilities.playerquest;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerQuestData {

    ArrayList<Quest> playerQuests = new ArrayList<>();

    public void startQuest(EntityPlayer player, Quest quest) {
        Quest clientQuest = QuestBuilder.fromNBT(quest.toNBT());
        clientQuest.setPlayer(player);
        clientQuest.start(player.world);
        player.getCapability(CapPlayerQuestData.PLAYER_QUEST_DATA, null).addQuest(clientQuest);
    }

    public int numberOfQuests() {
        return playerQuests.size();
    }

    public ArrayList<Quest> getQuests() {
        return new ArrayList<>(playerQuests);
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

    public Quest findQuest(int questID) {
        return playerQuests.stream().filter(quest -> quest.getQuestID() == questID).findFirst().orElse(null);
    }

    public List<ObjectiveBase> getAllCurrentObjectives() {
        return playerQuests.stream().map(Quest::getCurrentCheckpointObjectives).flatMap(Collection::stream).collect(Collectors.toList());
    }
}