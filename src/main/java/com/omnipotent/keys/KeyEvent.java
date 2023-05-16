package com.omnipotent.keys;

import com.omnipotent.Omnipotent;
import com.omnipotent.tools.Kaia;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.gui.GuiHandler.GuiIDs.ID_MOD;

public class KeyEvent {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (KeyInit.keyReturnKaia.isPressed()) {
            WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(player.dimension);;
            EntityPlayer serverPlayer = world.getPlayerEntityByUUID(player.getUniqueID());
            String name = serverPlayer.getName();
            String uuid = serverPlayer.getUniqueID().toString();
            if (!serverPlayer.world.isRemote) {
                List<EntityItem> EntityItems = world.loadedEntityList.stream().filter(entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof Kaia).map(entity -> ((EntityItem) entity)).collect(Collectors.toList());
                if (!EntityItems.isEmpty()) {
                    for (EntityItem entityItem : EntityItems) {
                        ItemStack kaiaStack = entityItem.getItem();
                        if (kaiaStack.hasTagCompound()) {
                            String nameOfKaia = kaiaStack.getTagCompound().getString("ownerName");
                            String uuidOfKaia = kaiaStack.getTagCompound().getString("ownerID");
                            if (name.equals(nameOfKaia) && uuid.equals(uuidOfKaia)) {
                                int emptySlot = serverPlayer.inventory.getFirstEmptyStack();
                                if (emptySlot != -1) {
                                    serverPlayer.inventory.setInventorySlotContents(emptySlot, kaiaStack);
                                } else {
                                    ItemStack item = serverPlayer.inventory.getStackInSlot(0);
                                    serverPlayer.dropItem(item, true);
                                    serverPlayer.inventory.setInventorySlotContents(0, kaiaStack);
                                }
                                entityItem.setDead();
                            }
                        }
                    }
                }
            }
        }
        if (KeyInit.keyOpenGuiKaia.isPressed() && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof Kaia) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, ID_MOD.ordinal(), player.world, 0, 0, 0);
        }
    }
}
