package com.danielcordell.minequest;

import com.danielcordell.minequest.entities.EntityNPC;
import com.danielcordell.minequest.questing.quest.Quest;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.*;

public class Util {
    public static UUID emptyUUID = new UUID(0, 0);

    public static Class<? extends EntityLivingBase> getEntityFromName(String name) {
        return EntityList.getClassFromName(name).asSubclass(EntityLivingBase.class);
    }

    public static String getNameFromEntity(Class<? extends EntityLivingBase> entity) {
        return EntityRegistry.getEntry(entity).getName();
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
        rewardItemRarities.add(Pair.of(new ItemStack(Items.WHEAT), 1));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.MELON), 2));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.IRON_INGOT), 4));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.GOLD_INGOT), 6));
        ItemStack itemStack = new ItemStack(Items.IRON_SWORD);
        itemStack.addEnchantment(Enchantments.SHARPNESS, 2);
        rewardItemRarities.add(Pair.of(itemStack, 10));
        rewardItemRarities.add(Pair.of(new ItemStack(Items.DIAMOND), 15));
        itemStack = new ItemStack(Items.DIAMOND_SWORD);
        itemStack.addEnchantment(Enchantments.SHARPNESS, 3);
        itemStack.addEnchantment(Enchantments.FIRE_ASPECT, 1);
        rewardItemRarities.add(Pair.of(itemStack, 25));
        itemStack = new ItemStack(Items.DIAMOND_CHESTPLATE);
        itemStack.addEnchantment(Enchantments.PROTECTION, 4);
        itemStack.addEnchantment(Enchantments.THORNS, 1);
        itemStack.addEnchantment(Enchantments.MENDING, 1);
        rewardItemRarities.add(Pair.of(itemStack, 100));
    }

    public static ArrayList<Pair<Item, Integer>> overworldItemDifficulties = new ArrayList<>();
    static {
        overworldItemDifficulties.add(Pair.of(Items.WHEAT, 1));
        overworldItemDifficulties.add(Pair.of(Items.MELON, 2));
        overworldItemDifficulties.add(Pair.of(Items.COAL, 2));
        overworldItemDifficulties.add(Pair.of(Items.IRON_INGOT, 4));
        overworldItemDifficulties.add(Pair.of(Items.GOLD_INGOT, 6));
        overworldItemDifficulties.add(Pair.of(Items.REDSTONE, 6));
        overworldItemDifficulties.add(Pair.of(Items.DIAMOND, 12));
        overworldItemDifficulties.add(Pair.of(new ItemBlock(Blocks.OBSIDIAN), 15));
        overworldItemDifficulties.add(Pair.of(Items.PRISMARINE_SHARD, 15));
        overworldItemDifficulties.add(Pair.of(Items.TOTEM_OF_UNDYING, 40));
        overworldItemDifficulties.add(Pair.of(Items.NETHER_STAR, 50));
    }

    public static ArrayList<Pair<Item, Integer>> netherItemDifficulties = new ArrayList<>();
    static {
        netherItemDifficulties.add(Pair.of(Items.NETHER_WART, 4));
        netherItemDifficulties.add(Pair.of(Items.QUARTZ, 4));
        netherItemDifficulties.add(Pair.of(Items.GLOWSTONE_DUST, 6));
        netherItemDifficulties.add(Pair.of(new ItemBlock(Blocks.MAGMA), 8));
        netherItemDifficulties.add(Pair.of(Items.BLAZE_ROD, 12));
    }
    public static ArrayList<Pair<Item, Integer>> endItemDifficulties = new ArrayList<>();
    static {
        endItemDifficulties.add(Pair.of(Items.CHORUS_FRUIT_POPPED, 6));
        endItemDifficulties.add(Pair.of(Items.END_CRYSTAL, 15));
        endItemDifficulties.add(Pair.of(Items.SHULKER_SHELL, 15));
        endItemDifficulties.add(Pair.of(Items.DRAGON_BREATH, 45));
        endItemDifficulties.add(Pair.of(new ItemBlock(Blocks.DRAGON_EGG), 50));
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
        switch (rand.nextInt(5)) {
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
}
