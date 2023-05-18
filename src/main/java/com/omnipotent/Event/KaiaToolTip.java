package com.omnipotent.Event;

import com.omnipotent.tools.Kaia;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.List;

public class KaiaToolTip {
    private int tick = 0;
    private int curColor = 0;
    private TextFormatting[] colors = {TextFormatting.YELLOW, TextFormatting.GOLD, TextFormatting.AQUA, TextFormatting.BLUE, TextFormatting.RED, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE};

    @SubscribeEvent
    public void kaiaToolTipRender(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof Kaia) {
            EntityPlayer player = null;
            if (event.getEntityPlayer() != null) {
                player = event.getEntityPlayer();
            }
            List<String> tooltip = event.getToolTip();
            for (int c = 0; c < tooltip.size(); c++) {
                String tipOfDisplay = tooltip.get(c);
                if (tipOfDisplay.endsWith(I18n.format("attribute.name.generic.attackDamage"))) {
                    String str = I18n.format("kaia.damage");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors[(curColor + i) % colors.length].toString());
                        sb.append(str.charAt(i));
                    }
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackDamage")));
                } else if (tipOfDisplay.endsWith(I18n.format("attribute.name.generic.attackSpeed"))) {
                    String str = I18n.format("kaia.speed");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors[(curColor + i) % colors.length].toString());
                        sb.append(str.charAt(i));
                    }
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackSpeed")));
                } else if (tipOfDisplay.endsWith("dono")) {
                    if (player == null) {
                        continue;
                    }
                    String str = "Falso Dono " + player.getName();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors[(curColor + i) % colors.length].toString());
                        sb.append(str.charAt(i));
                    }
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
                } else if (tipOfDisplay.endsWith("donoverdadeiro")) {
                    String str = "Verdadeiro Dono "+I18n.format("kaia.donoverdadeiro");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors[(curColor + i) % colors.length].toString());
                        sb.append(str.charAt(i));
                    }
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
                }
            }
        }
    }

    @SubscribeEvent
    public void ClientTick(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            if (++tick >= 3) {
                tick = 0;
                if (--curColor < 0) {
                    curColor = colors.length - 1;
                }
            }
        }
    }
}