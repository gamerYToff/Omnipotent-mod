package com.omnipotent;

import com.omnipotent.Event.KaiaEvent;
import com.omnipotent.Event.UpdateEntity;
import com.omnipotent.gui.GuiHandler;
import com.omnipotent.keys.KeyEvent;
import com.omnipotent.keys.KeyInit;
import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.omnipotent.gui.GuiHandler.GuiIDs.ID_MOD;

@Mod(modid = Omnipotent.MODID, name = Omnipotent.NAME, version = Omnipotent.VERSION)
@Mod.EventBusSubscriber
public class Omnipotent {
    public static final String MODID = "omnipotent";
    public static final String NAME = "Omnipotent Mod";
    public static final String VERSION = "1.0";
    public static final OmnipotentTab omnipotentTab = new OmnipotentTab("Omnipotent mod");

    public List kaiaList = new ArrayList();

    static Kaia kaia = new Kaia();
    @Mod.Instance(Omnipotent.MODID)
    public static Omnipotent instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(new KaiaEvent());
        MinecraftForge.EVENT_BUS.register(new KaiaUtil());
        MinecraftForge.EVENT_BUS.register(new UpdateEntity());
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(kaia);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new KeyEvent());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        KeyInit.initKeys();
    }

    @EventHandler
    public void posinit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) throws IOException {
        event.getRegistry().registerAll(kaia);
    }
}
