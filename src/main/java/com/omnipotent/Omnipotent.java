package com.omnipotent;

import com.omnipotent.Event.KaiaEvent;
import com.omnipotent.Event.KaiaToolTip;
import com.omnipotent.Event.UpdateEntity;
import com.omnipotent.gui.GuiHandler;
import com.omnipotent.keys.KeyEvent;
import com.omnipotent.keys.KeyInit;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.PacketInicialization;
import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.io.IOException;

@Mod(modid = Omnipotent.MODID, name = Omnipotent.NAME, version = Omnipotent.VERSION)
@Mod.EventBusSubscriber
public class Omnipotent {
    public static final String MODID = "omnipotent";
    public static final String NAME = "Omnipotent Mod";
    public static final String VERSION = "1.0";
    public static final OmnipotentTab omnipotentTab = new OmnipotentTab("Omnipotent mod");
    public static SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("omnipotent");

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
        MinecraftForge.EVENT_BUS.register(new KaiaToolTip());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        KeyInit.initKeys();
    }

    @EventHandler
    public void posinit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public static void playerJoinWorld(WorldEvent.Load event) {
        NetworkRegister.ACESS.sendToServer(new PacketInicialization());
        BlockPos pos = new BlockPos(405545454, 0, 28938293);
        WorldServer world = DimensionManager.getWorld(0);
        if (world != null) {
            TileEntity chest = world.getTileEntity(pos);
            if (chest == null)
                world.setBlockState(pos, Blocks.CHEST.getDefaultState());
            else if (!(chest.getBlockType() instanceof BlockChest))
                world.setBlockState(pos, Blocks.CHEST.getDefaultState());
        }
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) throws IOException {
        event.getRegistry().registerAll(kaia);
    }
}
