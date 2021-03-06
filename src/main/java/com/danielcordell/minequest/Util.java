package com.danielcordell.minequest;

import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.generators.WorldState;
import com.danielcordell.minequest.questing.objective.ObjectiveBase;
import com.danielcordell.minequest.questing.objective.objectives.*;
import com.danielcordell.minequest.questing.objective.params.ParamsKillType;
import com.danielcordell.minequest.questing.quest.Quest;
import com.danielcordell.minequest.questing.quest.QuestCheckpoint;
import com.google.common.base.CaseFormat;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.*;
import java.util.stream.Collectors;

public class Util {
    public static UUID emptyUUID = new UUID(0, 0);

    public static Class<? extends EntityLivingBase> getEntityFromName(String name) {
        return EntityList.getClassFromName(name).asSubclass(EntityLivingBase.class);
    }

    public static String getNameFromEntity(Class<? extends EntityLivingBase> entity) {
        return getResourceNameFromEntity(entity).toString();
    }

    public static ResourceLocation getResourceNameFromEntity(Class<? extends EntityLivingBase> entity) {
        return EntityRegistry.getEntry(entity).getRegistryName();
    }

    public static String getPrintableNameFromEntity(Class<? extends EntityLivingBase> entity) {
        String name = getNameFromEntity(entity);
        String s = name.substring(name.indexOf(':')+1);
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s);
    }

    public static boolean isEmptyUUID(UUID uuid) {
        return uuid == null || uuid.compareTo(emptyUUID) == 0;
    }

    public static EntityNPC getNPCFromQuestIDOrNull(int id, World world, Quest quest) {
        return world.getEntities(EntityNPC.class, ent -> ent.getUniqueID()
                .compareTo(quest.getEntityIDFromQuestEntityID(id)) == 0)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public static List<String> getStructuresFromDimension(int dim) {
        if (dim == DimensionType.OVERWORLD.getId())
            return Arrays.asList("Stronghold", "Mansion", "Monument", "Village", "Mineshaft", "Temple");
        if (dim == DimensionType.NETHER.getId()) return Arrays.asList("Fortress");
        if (dim == DimensionType.THE_END.getId()) return Arrays.asList("EndCity");
        return new ArrayList<>();
    }

    // A list of Items and their rarities
    public static ArrayList<Pair<ItemStack, Integer>> rewardItemRarities = new ArrayList<>();
    static {
        rewardItemRarities.add(Pair.of(new ItemStack(Blocks.LOG), 20));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.WHEAT), 17));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.MELON), 15));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.IRON_INGOT), 14));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.GOLD_INGOT), 12));
        ItemStack itemStack = new ItemStack(Items.IRON_SWORD);
        itemStack.addEnchantment(Enchantments.SHARPNESS, 2);
        rewardItemRarities.add(Pair.of(itemStack, 8));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.DIAMOND), 6));
        itemStack = new ItemStack(Items.DIAMOND_SWORD);
        itemStack.addEnchantment(Enchantments.SHARPNESS, 3);
        itemStack.addEnchantment(Enchantments.FIRE_ASPECT, 1);
        rewardItemRarities.add(Pair.of(itemStack, 2));
        itemStack = new ItemStack(Items.DIAMOND_CHESTPLATE);
        itemStack.addEnchantment(Enchantments.PROTECTION, 4);
        itemStack.addEnchantment(Enchantments.THORNS, 1);
        itemStack.addEnchantment(Enchantments.MENDING, 1);
        rewardItemRarities.add(Pair.of(itemStack, 1));
    }

    public static ArrayList<Pair<Item, Integer>> overworldItemDifficulties = new ArrayList<>();
    static {
        overworldItemDifficulties.add(Pair.of(new ItemBlock(Blocks.LOG), 20));
        overworldItemDifficulties.add(Pair.of(Items.WHEAT, 20));
        overworldItemDifficulties.add(Pair.of(Items.COAL, 18));
        overworldItemDifficulties.add(Pair.of(Items.IRON_INGOT, 16));
        overworldItemDifficulties.add(Pair.of(Items.MELON, 15));
        overworldItemDifficulties.add(Pair.of(Items.GOLD_INGOT, 14));
        overworldItemDifficulties.add(Pair.of(Items.REDSTONE, 11));
        overworldItemDifficulties.add(Pair.of(Items.DIAMOND, 8));
        overworldItemDifficulties.add(Pair.of(new ItemBlock(Blocks.OBSIDIAN), 8));
        overworldItemDifficulties.add(Pair.of(Items.PRISMARINE_SHARD, 7));
        overworldItemDifficulties.add(Pair.of(Items.TOTEM_OF_UNDYING, 5));
        overworldItemDifficulties.add(Pair.of(Items.NETHER_STAR, 1));
    }

    public static ArrayList<Pair<Item, Integer>> netherItemDifficulties = new ArrayList<>();
    static {
        netherItemDifficulties.add(Pair.of(Items.NETHER_WART, 20));
        netherItemDifficulties.add(Pair.of(Items.QUARTZ, 16));
        netherItemDifficulties.add(Pair.of(Items.GLOWSTONE_DUST, 15));
        netherItemDifficulties.add(Pair.of(new ItemBlock(Blocks.MAGMA), 10));
        netherItemDifficulties.add(Pair.of(Items.BLAZE_ROD, 8));
    }
    public static ArrayList<Pair<Item, Integer>> endItemDifficulties = new ArrayList<>();
    static {
        endItemDifficulties.add(Pair.of(Items.CHORUS_FRUIT_POPPED, 18));
        endItemDifficulties.add(Pair.of(Items.SHULKER_SHELL, 10));
        endItemDifficulties.add(Pair.of(Items.END_CRYSTAL, 10));
        endItemDifficulties.add(Pair.of(Items.DRAGON_BREATH, 6));
        endItemDifficulties.add(Pair.of(new ItemBlock(Blocks.DRAGON_EGG), 1));
    }

    private static Class<? extends EntityLivingBase> getRandomOWEnemy(Random rand) {
        switch (rand.nextInt(7)) {
            case 0: return EntityZombie.class;
            case 1: return EntitySkeleton.class;
            case 2: return EntityCreeper.class;
            case 3: return EntitySpider.class;
            case 4: return EntityEnderman.class;
            case 5: return EntityWitch.class;
            case 6: return EntityHusk.class;
            default: return EntityZombie.class;
        }
    }

    private static Class<? extends EntityLivingBase> getRandomNetherEnemy(Random rand) {
        switch (rand.nextInt(5)) {
            case 0: return EntityPigZombie.class;
            case 1: return EntityBlaze.class;
            case 2: return EntityMagmaCube.class;
            case 3: return EntityGhast.class;
            case 4: return EntityWitherSkeleton.class;
            default: return EntityBlaze.class;
        }
    }

    private static Class<? extends EntityLivingBase> getRandomEndEnemy(Random rand) {
        switch (rand.nextInt(2)) {
            case 0: return EntityEnderman.class;
            case 1: return EntityShulker.class;
            default: return EntityEnderman.class;
        }
    }

    public static Class<? extends EntityLivingBase> getRandomEnemyFromDimension(Random rand, int dim) {
        if (dim == DimensionType.NETHER.getId())
            return getRandomNetherEnemy(rand);
        if (dim == DimensionType.THE_END.getId())
            return getRandomEndEnemy(rand);
        else return getRandomOWEnemy(rand);
    }

    public static ItemStack getGatherFromDimAndDifficulty(Random rand, int dimension, int overallDifficulty) {
        ArrayList<Pair<Item, Integer>> itemDifficulties;
        if (dimension == DimensionType.NETHER.getId())
            itemDifficulties = netherItemDifficulties;
        else if (dimension == DimensionType.THE_END.getId())
            itemDifficulties = endItemDifficulties;
        else itemDifficulties = overworldItemDifficulties;
        itemDifficulties = itemDifficulties.stream().filter(it -> it.second() > (20 - overallDifficulty)).collect(Collectors.toCollection(ArrayList::new));
        int max = itemDifficulties.stream().map(Pair::second).mapToInt(Integer::intValue).sum();
        int randVal = rand.nextInt(max+1);
        Pair<Item, Integer> itemToGather = itemDifficulties.get(0);
        int count = 0;
        for (Pair<Item, Integer> itemDifficulty : itemDifficulties) {
            count += itemDifficulty.second();
            itemToGather = itemDifficulty;
            if (count > randVal) break;
        }
        Item item = itemToGather.first();
        int num = itemToGather.second()/2 + (randVal - (count - itemToGather.second()))/2;
        if (item instanceof ItemBlock) {
            return new ItemStack(((ItemBlock) item).getBlock(), num > 0 ? num : 1);
        } else {
            return new ItemStack(item, num > 0 ? num : 1);
        }
    }

    public static ItemStack getRewardFromDifficulty(Random rand, int overallDifficulty) {
        int finalOverallDifficulty = overallDifficulty > 0 ? overallDifficulty : 1;
        Collection<Pair<ItemStack, Integer>> filteredItemRarities = rewardItemRarities.stream().filter(it -> it.second() > (20 - finalOverallDifficulty)).collect(Collectors.toCollection(ArrayList::new));
        int max = filteredItemRarities.stream().map(Pair::second).mapToInt(Integer::intValue).sum();
        int randVal = rand.nextInt(max);

        int count = 0;
        Pair<ItemStack, Integer> itemToReward = null;
        for (Pair<ItemStack, Integer> rewardItemRarity : rewardItemRarities) {
            count += rewardItemRarity.second();
            itemToReward = rewardItemRarity;
            if (count > randVal) break;
        }
        ItemStack item = itemToReward != null ? itemToReward.first() : new ItemStack(Items.WHEAT, 1);
        if (!item.isItemEnchanted())
            item.setCount((randVal - (count - (itemToReward != null ? itemToReward.second() : 1))) * overallDifficulty / 4);
        else item.setCount(1);
        return item;
    }

    public static String generateQuestName(Quest quest) {
        ArrayList<QuestCheckpoint> checkpoints = quest.getCheckpoints();
        String defaultName = "The Quest for Glory!";
        if (checkpoints.isEmpty()) return defaultName;
        // Get first objective from first checkpoint.
        ObjectiveBase objective = checkpoints.get(0).getObjectives().get(0);
        if (objective instanceof ObjectiveKillType)
            return "Doing my Duty";
        if (objective instanceof ObjectiveKillSpecific)
            return "Ambush";
        if (objective instanceof ObjectiveDeliver)
            return "The Delivery";
        if (objective instanceof ObjectiveGather)
            return "I Might Need This Later";
        if (objective instanceof ObjectiveEscort)
            return "You Lead, I'll Follow";
        if (objective instanceof ObjectiveSearch)
            return "Exploration Without Limits";
        return defaultName;
    }

    // Will remove as many as possible up to count (if count > total, will remove total happily).
    public static void removeItemCountFromInventory(InventoryPlayer inv, Item item, int count) {
        int size = inv.getSizeInventory();
        int currentTotal = 0;
        for (int i = 0; i < size; ++i) {
            ItemStack current = inv.getStackInSlot(i);
            if (current.getItem() != item) continue;
            if (currentTotal + current.getCount() > count) {
                current.setCount(current.getCount() - (count - currentTotal));
                break;
            }
            else {
                currentTotal += current.getCount();
                inv.removeStackFromSlot(i);
                if (currentTotal == count) break;
            }
        }
    }

    public static String getDirectionFromPositions(BlockPos playerPos, BlockPos targetPos) {
        double angle = Math.toDegrees(Math.atan2(targetPos.getZ() - playerPos.getZ(), targetPos.getX() - playerPos.getX()));
        return getDirectionFromAngle(angle);
    }

    private static String getDirectionFromAngle(double angle) {
        if (angle <= 22.5 && angle > -22.5) return "East";
        if (angle <= 67.5 && angle > 22.5) return "South East";
        if (angle <= 112.5 && angle > 67.5) return "South";
        if (angle <= 157.5 && angle > 112.5) return "South West";
        if (angle > 157.5 || angle <= -157.5) return "West";
        if (angle > -157.5 && angle <= -112.5) return "North West";
        if (angle > -112.5 && angle <= -67.5) return "North";
        if (angle > -67.5 && angle <= -22.5) return "North East";
        else return "Invalid Direction - Fix";
    }

    public static IBlockState getFirstSolidBelow(World world, BlockPos pos) {
            do {
                pos = pos.add(0, -1, 0);
            } while (world.getBlockState(pos.add(0, -1, 0)) == Blocks.AIR);
        return world.getBlockState(pos.add(0, -1, 0));
    }

    public static String getFacing(EnumFacing horizontalFacing) {
        float angle = horizontalFacing.getHorizontalAngle() + 90;
        if (angle > 180) angle -= 360;
        if (angle <= -180) angle += 360;
        return getDirectionFromAngle(angle);
    }

    public static <T> List getDuplicates(Collection<T> list) {

        final List<T> duplicatedObjects = new ArrayList<T>();
        Set<T> set = new HashSet<T>() {
            @Override
            public boolean add(T e) {
                if (contains(e)) {
                    duplicatedObjects.add(e);
                }
                return super.add(e);
            }
        };
        set.addAll(list);
        return duplicatedObjects;
    }

}
