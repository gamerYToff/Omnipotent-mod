package com.omnipotent.keys;

import com.omnipotent.Omnipotent;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.ReturnKaiaPacket;
import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.omnipotent.Omnipotent.channel;
import static com.omnipotent.gui.GuiHandler.GuiIDs.ID_MOD;

public class KeyEvent {
    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (KeyInit.keyReturnKaia.isPressed()) {
            NetworkRegister.ACESS.sendToServer(new ReturnKaiaPacket());
        }
        if (KeyInit.keyOpenGuiKaia.isPressed() && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof Kaia) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, ID_MOD.ordinal(), player.world, 0, 0, 0);
        }
    }
}
