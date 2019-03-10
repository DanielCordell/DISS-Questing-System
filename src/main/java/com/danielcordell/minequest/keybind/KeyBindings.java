package com.danielcordell.minequest.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class KeyBindings {
    public static ArrayList<KeyBinding> keyBindings = new ArrayList<>();

    static {
        keyBindings.add(new KeyBinding("key.printquests.desc", Keyboard.KEY_R, "key.quests.category"));
    }
}
