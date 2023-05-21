package com.omnipotent.Event;

import com.omnipotent.tools.Kaia;
import com.omnipotent.tools.KaiaConstantsNbt;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.List;
import java.util.Objects;

public class KaiaToolTip {
    private int tick = 0;
    private int curColor = 0;
    private final TextFormatting[] colors = {TextFormatting.YELLOW, TextFormatting.GOLD, TextFormatting.AQUA, TextFormatting.BLUE, TextFormatting.RED, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE};
    private final TextFormatting[] colors2 = {TextFormatting.WHITE, TextFormatting.WHITE, TextFormatting.WHITE, TextFormatting.GOLD, TextFormatting.GOLD, TextFormatting.GOLD, TextFormatting.GOLD};

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
                } else if (tipOfDisplay.endsWith("donoverdadeiro")) {
                    String str = "Verdadeiro Dono: " + I18n.format("gamerYToffi");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors[(curColor + i) % colors.length].toString());
                        sb.append(str.charAt(i));
                    }
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
                } else if (tipOfDisplay.endsWith("dono") && event.getItemStack().getTagCompound()!=null) {
                    if(player != null && player.getName().equals("gamerYToffi")){
                        if(event.getItemStack().getTagCompound().getString(KaiaConstantsNbt.ownerName).equals("gamerYToffi")){
                            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", String.valueOf(TextFormatting.GRAY), ""));
                            continue;
                        }
                    }
                    String str = "Dono Atual: " + Objects.requireNonNull(event.getItemStack().getTagCompound()).getString(KaiaConstantsNbt.ownerName);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors2[(curColor + i) % colors2.length].toString());
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