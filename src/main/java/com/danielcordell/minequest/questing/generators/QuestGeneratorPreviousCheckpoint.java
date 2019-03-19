package com.danielcordell.minequest.questing.generators;

import com.danielcordell.minequest.MineQuest;
import com.danielcordell.minequest.Util;
import com.danielcordell.minequest.questing.enums.ObjectiveType;
import com.danielcordell.minequest.questing.intent.intents.IntentGiveItemStack;
import com.danielcordell.minequest.questing.intent.intents.IntentSpawnEntity;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.ObjectiveBuilder;
import com.danielcordell.minequest.questing.objective.ObjectiveParamsBase;
import com.danielcordell.minequest.questing.objective.params.*;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import com.danielcordell.minequest.worlddata.WorldQuestData;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class QuestGeneratorPreviousCheckpoint {

    public static Quest generate(WorldServer world, EntityPlayerMP player) {
        Quest quest = Quest.newEmptyQuest(world);
        QuestCheckpoint firstCheckpoint = new QuestCheckpoint(quest);
        WorldState worldState = WorldState.getWorldState(world, player);
        ConcurrentHashMap<ObjectiveType, Integer> objectiveWeights = ObjectiveType.getObjectiveWeightMap(worldState);

        MineQuest.logger.info("Difficulty: " + worldState.overallDifficulty);

        //Determine Objective for first Checkpoint
        int max = objectiveWeights.values().stream().mapToInt(Integer::intValue).sum();
        int randVal = ObjectiveGenerator.rand.nextInt(max+1);
        ObjectiveType objectiveType = null;
        int count = 0;
        MineQuest.logger.info("randVal: " + randVal);
        MineQuest.logger.info("New Weights:");
        for (Map.Entry<ObjectiveType, Integer> entry : objectiveWeights.entrySet()) {
            count += entry.getValue();
            objectiveType = entry.getKey();
            MineQuest.logger.info(entry.getKey().name() + ": " + entry.getValue());
            if (count > randVal) break;
        }
        if (objectiveType == null) {
            MineQuest.logger.error("Starting 'objectiveType' is null!");
            MineQuest.logger.error("Defaulting to KillType");
            objectiveType = ObjectiveType.KILL_TYPE;
        }

        if (objectiveType == ObjectiveType.TRIGGER) {
            MineQuest.logger.error("Starting 'objectiveType' is null!");
            MineQuest.logger.error("Defaulting to KillType");
            objectiveType = ObjectiveType.KILL_TYPE;
        }

        ObjectiveBase objective = makeObjectiveFromWorldState(objectiveType, worldState, firstCheckpoint);
        firstCheckpoint.addObjective(objective);
        quest.addCheckpoint(firstCheckpoint);

        QuestCheckpoint chkpnt = firstCheckpoint;

        for (int i = 0; i < worldState.overallDifficulty / 4 + ObjectiveGenerator.rand.nextInt(2); ++i) {
            chkpnt = iterate(worldState, chkpnt);
            quest.addCheckpoint(chkpnt);
        }

        quest.addFinishIntent(new IntentGiveItemStack(quest, Util.getRewardFromDifficulty(ObjectiveGenerator.rand, worldState.overallDifficulty)));

        quest.setQuestName(Util.generateQuestName(quest));

        return quest;
    }

    private static QuestCheckpoint iterate(WorldState worldState, QuestCheckpoint prevCheckpoint) {
        List<ObjectiveParamsBase> prevParams = prevCheckpoint.getObjectives().stream().map(ObjectiveBase::getParams).collect(Collectors.toList());
        Collections.shuffle(prevParams);
        QuestCheckpoint newCheckpoint = new QuestCheckpoint(prevCheckpoint.getQuest());

        Random rand = ObjectiveGenerator.rand;
        int numberOfObjectives = worldState.overallDifficulty/4 + rand.nextInt(worldState.overallDifficulty/4 + 1);
        int[] numToGeneratePerOriginal = new int[prevParams.size()];
        for (int i = 0; i < numberOfObjectives; ++i) {
            numToGeneratePerOriginal[i%prevParams.size()]++;
        }

        ArrayList<Item> allItemsUsed = new ArrayList<>();

        for (int i = 0; i < prevParams.size(); ++i) {
            for (int j = 0; j < numToGeneratePerOriginal[i]; ++j) {
                ObjectiveParamsBase param = prevParams.get(i);
                ObjectiveParamsBase newParams = null;
                if (param instanceof ParamsKillType) {
                    Class<? extends EntityLivingBase> entity;
                    do {
                        entity = Util.getRandomEnemyFromDimension(rand, worldState.dimension);
                    } while (entity == ((ParamsKillType) param).entityTypeToKill);

                    int numToKill = (rand.nextInt(3) + 3) * worldState.overallDifficulty / 2;
                    numToKill = numToKill > 0 ? numToKill : 1;
                    newParams = new ParamsKillType(newCheckpoint, "Keep cleansing the world!")
                            .setParamDetails(entity, numToKill);
                }
                else if (param instanceof ParamsKillSpecific) {
                    IntentSpawnEntity oldIntent = prevCheckpoint.getIntents().stream()
                            .filter(it -> it instanceof IntentSpawnEntity)
                            .map(it -> ((IntentSpawnEntity) it))
                            .filter(it -> it.getEntityData().equals(((ParamsKillSpecific) param).nbtTagToFind)).findFirst().orElse(null);
                    Class<? extends EntityLivingBase> entityToSpawn;
                    if (oldIntent == null) {
                        entityToSpawn = Util.getRandomEnemyFromDimension(rand, worldState.dimension);
                    }
                    else {
                        entityToSpawn = oldIntent.getEntityToSpawn();
                    }
                    int numToKill = (rand.nextInt(3) + 3) * worldState.overallDifficulty / 2;
                    numToKill = numToKill > 0 ? numToKill : 1;
                    newParams = new ParamsKillType(newCheckpoint, "Get Revenge!")
                            .setParamDetails(entityToSpawn, numToKill);

                }
                else if (param instanceof ParamsGather) {
                    ItemStack itemStack = ((ParamsGather) param).item;
                    if (rand.nextInt(4) == 0) {
                        // Deliver the gathered items to an NPC
                        newParams = ObjectiveGenerator.generateDeliverObjective(newCheckpoint, worldState, ((ParamsGather) param).item);
                    } else {
                        List<IRecipe> allRecipes = ForgeRegistries.RECIPES.getEntries()
                                .stream()
                                .map(Map.Entry::getValue)
                                .collect(Collectors.toList());
                        Collections.shuffle(allRecipes);
                        IRecipe recipe = null;
                        for (IRecipe iRecipe : allRecipes) {
                            List<Item> items = iRecipe.getIngredients()
                                    .stream()
                                    .map(Ingredient::getMatchingStacks)
                                    .flatMap(Arrays::stream)
                                    .map(ItemStack::getItem)
                                    .collect(Collectors.toList());
                            Item item = itemStack.getItem();
                            if (items.contains(item) && !allItemsUsed.contains(item)) {
                                recipe = iRecipe;
                                allItemsUsed.add(item);
                                break;
                            }
                        }
                        if (recipe == null)
                            newParams = ObjectiveGenerator.generateDeliverObjective(newCheckpoint, worldState, itemStack);
                        else
                            newParams = ObjectiveGenerator.generateGatherObjective(newCheckpoint, recipe.getRecipeOutput());
                    }
                }
                else if (param instanceof ParamsDeliver) {
                    if (rand.nextInt(2) == 0) {
                        // Deliver something else to the same NPC.
                        ItemStack item = Util.getGatherFromDimAndDifficulty(rand, worldState.dimension, worldState.overallDifficulty);
                        newParams = ObjectiveGenerator.generateDeliverObjective(newCheckpoint, worldState, item, ((ParamsDeliver) param).questEntityID);
                        newParams.description = "Gather the NPC some more items!";
                    } else {
                        // Take that NPC somewhere (not a village).
                        List<Pair<String, Double>> structures = worldState.closestStructurePerType
                                .entrySet()
                                .stream()
                                .filter(it -> !it.getKey().equalsIgnoreCase("village") && !it.getValue().second())
                                .map(it -> Pair.of(it.getKey(), Math.sqrt(worldState.playerPos.distanceSq(it.getValue().first()))))
                                .sorted(Comparator.comparing(Pair::second))
                                .collect(Collectors.toList());
                        String structure = structures.get(0).first();
                        newParams = ObjectiveGenerator.generateEscortObjective(newCheckpoint, worldState, structure, ((ParamsDeliver) param).questEntityID);
                        newParams.description = "Now the NPC is stocked up, escort them to a " + structure;
                    }
                }
                else if (param instanceof ParamsEscort) {
                    if (rand.nextInt(5) == 0) {
                        // Go somewhere else
                        String structure;
                        List<String> structures = Util.getStructuresFromDimension(worldState.dimension);
                        do {
                            structure = structures.get(rand.nextInt(structures.size()));
                        } while (structure.equalsIgnoreCase(((ParamsEscort) param).type));
                        newParams = ObjectiveGenerator.generateEscortObjective(newCheckpoint, worldState, structure, ((ParamsEscort) param).questEntityID);
                        newParams.description = "The NPC wants to keep exploring, take them to a " + structure + "!";
                    } else {
                        // Give me resources
                        ItemStack item = Util.getGatherFromDimAndDifficulty(rand, worldState.dimension, worldState.overallDifficulty);
                        newParams = ObjectiveGenerator.generateDeliverObjective(newCheckpoint, worldState, item, ((ParamsDeliver) param).questEntityID);
                        newParams.description = "The NPC wants to set up camp, gather them some resources!";
                    }
                } else if (param instanceof ParamsSearch) {
                    if (rand.nextInt(5) == 0) {
                        // Find a new structure.
                        String structure;
                        List<String> structures = Util.getStructuresFromDimension(worldState.dimension);
                        do {
                            structure = structures.get(rand.nextInt(structures.size()));
                        } while (structure.equalsIgnoreCase(((ParamsSearch) param).structureType));
                        newParams = ObjectiveGenerator.generateSearchObjective(newCheckpoint, structure);
                        newParams.description = "Lets keep exploring, try to find a " + structure + "!";
                    } else {
                        // Ambush!
                        newParams = ObjectiveGenerator.generateKillSpecificObjective(newCheckpoint, worldState, Util.getRandomEnemyFromDimension(rand, worldState.dimension));
                    }
                } else {
                    newParams = ObjectiveGenerator.generateGatherObjective(newCheckpoint, new ItemStack(Items.APPLE));
                }
                newCheckpoint.addObjective(ObjectiveBuilder.fromParams(newParams));
            }
        }
        return newCheckpoint;
    }

    private static ObjectiveBase makeObjectiveFromWorldState(ObjectiveType objectiveType, WorldState worldState, QuestCheckpoint firstCheckpoint) {
        ObjectiveParamsBase params;
        if (objectiveType == ObjectiveType.KILL_TYPE) {
            if (worldState.inOrNextToSlimeChunk & ObjectiveGenerator.rand.nextInt(8) == 0)
                params = new ParamsKillType(firstCheckpoint, "You're near a Slime Chunk, kill some slimes!").setParamDetails(EntitySlime.class, worldState.overallDifficulty);
            else if (!worldState.nearbySpawners.isEmpty()){
                BlockPos pos = worldState.nearbySpawners.get(ObjectiveGenerator.rand.nextInt(worldState.nearbySpawners.size()));
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) worldState.world.getTileEntity(pos);
                Class<? extends EntityLivingBase> spawnerEntity = spawner.getSpawnerBaseLogic().getCachedEntity().getClass().asSubclass(EntityLivingBase.class);
                params = new ParamsKillType(firstCheckpoint, "You're very close to a Spawner, kill some " + Util.getPrintableNameFromEntity(spawnerEntity) + "s!").setParamDetails(spawnerEntity, worldState.overallDifficulty / 2 + 5);
            }
            else {
                int numToKill = (ObjectiveGenerator.rand.nextInt(3) + 3) * worldState.overallDifficulty / 2;
                numToKill = numToKill > 0 ? numToKill : 1;
                params = new ParamsKillType(firstCheckpoint, "Kill some enemies!").setParamDetails(
                        Util.getRandomEnemyFromDimension(ObjectiveGenerator.rand, worldState.dimension), numToKill
                );
            }
        } else if (objectiveType == ObjectiveType.KILL_SPECIFIC) {
            Class<? extends EntityLivingBase> entType = Util.getRandomEnemyFromDimension(ObjectiveGenerator.rand, worldState.dimension);
            params = ObjectiveGenerator.generateKillSpecificObjective(firstCheckpoint, worldState, entType);
        } else if (objectiveType == ObjectiveType.TRIGGER) {
            throw new NotImplementedException("Not implemented this objective yet");
            //params = new ParamsKillSpecific(firstCheckpoint, "Trigger something!");
        } else if (objectiveType == ObjectiveType.ESCORT) {
            List<String> structures = Util.getStructuresFromDimension(worldState.dimension);
            String structure = null;
            while (structure == null || worldState.closestStructurePerType.get(structure).second()) {
                structure = structures.get(ObjectiveGenerator.rand.nextInt(structures.size()));
            }
            //?Todo pick an NPC in the world, if there is an NPC NOT currently in an open quest and not too far away (farther than closest village) then use them, otherwise make a new one at the closest village.
            params = ObjectiveGenerator.generateEscortObjective(firstCheckpoint, worldState, structure);
        } else if (objectiveType == ObjectiveType.GATHER) {
            ItemStack itemStack = Util.getGatherFromDimAndDifficulty(ObjectiveGenerator.rand, worldState.dimension, worldState.overallDifficulty);
            params = ObjectiveGenerator.generateGatherObjective(firstCheckpoint, itemStack);
        } else if (objectiveType == ObjectiveType.SEARCH) {
            List<String> structures = Util.getStructuresFromDimension(worldState.dimension);
            String structure = null;
            while (structure == null || worldState.closestStructurePerType.get(structure).second()) {
                structure = structures.get(ObjectiveGenerator.rand.nextInt(structures.size()));
            }
            params = ObjectiveGenerator.generateSearchObjective(firstCheckpoint, structure);
        } else if (objectiveType == ObjectiveType.DELIVER) {
            ItemStack itemStack = Util.getGatherFromDimAndDifficulty(ObjectiveGenerator.rand, worldState.dimension, worldState.overallDifficulty);
            params = ObjectiveGenerator.generateDeliverObjective(firstCheckpoint, worldState, itemStack);
        }
        else {
            MineQuest.logger.error("Could not construct an objective, bad ObjectiveType");
            return null;
        }
        return ObjectiveBuilder.fromParams(params);
    }

}