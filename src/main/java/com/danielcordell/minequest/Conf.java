package com.danielcordell.minequest;

import net.minecraftforge.common.config.Config;

@Config(modid = MineQuest.MODID)
public class Conf {
    @Config.Comment("True for linear quest generation (checkpoints generate based on previous).\nFalse for random quest generation (checkpoints generate based on random")
    public static boolean shouldGenerateLinear = true;

    @Config.Comment("Should difficulty increase be faster")
    public static boolean shouldDifficultyScaleFaster = true;
}

