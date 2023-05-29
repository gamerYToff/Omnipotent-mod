package com.omnipotent.gui;

import com.omnipotent.init.InitButtonsForGuiKaia;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.tools.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class KaiaGui extends GuiScreen {
    EntityPlayer player = null;
    public static int width;
    public static int height;
    public static FontRenderer fontRenderer;
    private InitButtonsForGuiKaia initButtonsForGuiKaia;

    public KaiaGui(InventoryPlayer inventoryPlayer, ItemStack itemStack) {
        this.player = inventoryPlayer.player;
    }

    @Override
    public void initGui() {
        super.initGui();
        width = super.width;
        height = super.height;
        fontRenderer = super.fontRenderer;
        this.initButtonsForGuiKaia = new InitButtonsForGuiKaia();
        initButtonsForGuiKaia.init(player, buttonList);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        initButtonsForGuiKaia.drawButtons(Minecraft.getMinecraft(), mouseX, mouseY, (int) partialTicks);
        initButtonsForGuiKaia.drawLabels(fontRenderer);
        initButtonsForGuiKaia.drawGuiText();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        for (String name : initButtonsForGuiKaia.namesOfGuiTextList) {
            initButtonsForGuiKaia.guiTextFieldList.get(name).mouseClicked(mouseX, mouseY, button);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (String name : initButtonsForGuiKaia.namesOfGuiTextList) {
            initButtonsForGuiKaia.guiTextFieldList.get(name).textboxKeyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        if (isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(0)).getText())) {
            int areaBloco = KaiaUtil.getKaiaInMainHand(player).getTagCompound().getInteger(KaiaConstantsNbt.blockBreakArea);
            int valueButtonBlockArea = Integer.parseInt(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(0)).getText());
            if (valueButtonBlockArea % 2 == 0) {
                areaBloco = --valueButtonBlockArea;
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.blockBreakArea, areaBloco));
            } else {
                areaBloco = valueButtonBlockArea;
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.blockBreakArea, areaBloco));
            }
        }
        if (isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(1)).getText())) {
            int rangeAttack = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(1)).getText());
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.rangeAttack, rangeAttack));
        }
        if (isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(2)).getText())) {
            int distance = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(2)).getText());
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket("blockReachDistance", distance));
        }
        super.onGuiClosed();
    }

    public static boolean isJustNumber(String text) {
        return text.matches("[0-9]+");
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (initButtonsForGuiKaia.buttonsList.contains(button)) {
            initButtonsForGuiKaia.functionsForButtonsList.get(button).run();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
