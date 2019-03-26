package com.danielcordell.minequest.questing.capabilities;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.questing.enums.QuestState;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerQuestData {

    ArrayList<Quest> playerQuests = new ArrayList<>();
    boolean canGenerate = false;
    long timeLastGenerated = 0;
    int timeUntilNext = 0;

    public void startQuest(EntityPlayer player, Quest quest) {
        //Quest Duplication in case it's a global quest and not a player-specific one
        Quest dupeQuest = QuestBuilder.fromNBT(quest.toNBT());
        dupeQuest.setPlayer(player);
        dupeQuest.start(player.world);
        addQuest(dupeQuest);

        EntityPlayerMP playerMP = ((EntityPlayerMP) player);

        SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("Quest Started").setStyle(new Style().setColor(TextFormatting.WHITE)));
        playerMP.connection.sendPacket(packet);
        packet = new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(quest.getName()).setStyle(new Style().setColor(TextFormatting.GRAY).setItalic(true)));
        playerMP.connection.sendPacket(packet);

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
        } else playerQuests.add(quest);
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

    public void updateAllCurrentObjectives(Event event) {
        playerQuests.stream().map(Quest::getCurrentCheckpointObjectives).flatMap(Collection::stream).forEach(objective -> objective.update(event));
    }

    public boolean canGenerate() {
        return canGenerate;
    }

    public void startGenerating() {
        canGenerate = true;
    }

    public long getTimeLastGenerated() {
        return timeLastGenerated;
    }

    public int getTimeUntilNext() {
        return timeUntilNext;
    }

    public void setTimeLastGenerated(TickEvent.PlayerTickEvent event, long timeLastGenerated) {
        this.timeLastGenerated = timeLastGenerated;
    }

    public void setTimeUntilNext(TickEvent.PlayerTickEvent event, int timeUntilNext) {
        this.timeUntilNext = timeUntilNext;
    }

    public long getNumberOfActiveQuests() {
        return playerQuests.stream().filter(it -> it.getState() == QuestState.STARTED).count();
    }

    public void copyTo(PlayerQuestData capNew) {
        capNew.canGenerate          = this.canGenerate;
        capNew.playerQuests         = this.playerQuests;
        capNew.timeUntilNext        = this.timeUntilNext;
        capNew.timeLastGenerated    = this.timeLastGenerated;

    }
}
