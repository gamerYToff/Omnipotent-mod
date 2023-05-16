package com.omnipotent.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyInit {
    public static KeyBinding keyOpenGuiKaia = new KeyBinding("Configurações", Keyboard.KEY_R, "Botões Kaia");
    public static KeyBinding keyReturnKaia = new KeyBinding("Retorna Kaia", Keyboard.KEY_G, "Botões Kaia");

    public static void initKeys(){
        ClientRegistry.registerKeyBinding(keyReturnKaia);
        ClientRegistry.registerKeyBinding(keyOpenGuiKaia);
    }
}
