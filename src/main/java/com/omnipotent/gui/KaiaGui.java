package com.omnipotent.gui;

import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.nbtpackets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KaiaGui extends GuiScreen {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    int BUTTON_ID = 0;
    int TEXFIELD_ID = 0;
    int areaBloco = 0;
    ItemStack kaiaItem = null;
    EntityPlayer player = null;
    InventoryPlayer inventoryPlayer = null;
    GuiButton next = null;
    GuiButton pre = null;
    private final List<GuiTextField> guiTextFieldList = new ArrayList<>();
    private final List<GuiButton> guiButtonList = new ArrayList<>();
    private final HashMap<GuiButton, Integer> dimensionHash = new HashMap();

    public KaiaGui(InventoryPlayer inventoryPlayer, ItemStack itemStack) {
        this.inventoryPlayer = inventoryPlayer;
        this.player = inventoryPlayer.player;
        this.kaiaItem = itemStack;
        this.areaBloco = itemStack.getTagCompound().getInteger("areaBloco");
    }

    @Override
    public void initGui() {
        super.initGui();
        int rangeAttack = this.kaiaItem.getTagCompound().getInteger("rangeAttack");
        guiTextFieldList.add(new GuiTextField(TEXFIELD_ID, fontRenderer, width / 2 - 100, height / 2 - 90, 200, 20));
        guiTextFieldList.get(0).setMaxStringLength(10);
        guiTextFieldList.get(0).setFocused(false);
        guiTextFieldList.get(0).setText(String.valueOf(this.areaBloco));

        guiTextFieldList.add(new GuiTextField(TEXFIELD_ID++, fontRenderer, width / 2 - 100, height / 2 - 65, 200, 20));
        guiTextFieldList.get(1).setMaxStringLength(100);
        guiTextFieldList.get(1).setFocused(false);
        guiTextFieldList.get(1).setText(String.valueOf(rangeAttack));

        guiTextFieldList.add(new GuiTextField(TEXFIELD_ID++, fontRenderer, width / 2 - 90, height / 2, 50, 20));
        guiTextFieldList.get(2).setMaxStringLength(100);
        guiTextFieldList.get(2).setFocused(false);

        guiTextFieldList.add(new GuiTextField(TEXFIELD_ID++, fontRenderer, width / 2 - 35, height / 2, 50, 20));
        guiTextFieldList.get(3).setMaxStringLength(100);
        guiTextFieldList.get(3).setFocused(false);

        guiTextFieldList.add(new GuiTextField(TEXFIELD_ID++, fontRenderer, width / 2 - -20, height / 2, 50, 20));
        guiTextFieldList.get(4).setMaxStringLength(100);
        guiTextFieldList.get(4).setFocused(false);


        guiButtonList.add(new GuiButton(0, width / 2 - 62, height / 2 - 38, 30, 15, String.valueOf(player.getHeldItemMainhand().getTagCompound().getBoolean("killFriendEntities"))));
        guiButtonList.add(new GuiButton(1, width / 2 - 62, height / 2 - 19, 30, 15, String.valueOf(player.getHeldItemMainhand().getTagCompound().getBoolean("killAllEntities"))));
        guiButtonList.add(new GuiButton(2, width / 2 - -73, height / 2 - -3, 44, 15, "concluir"));
        DimensionType[] dimensionIds = DimensionType.values();
        int x = 223;
        int count = 2;
        int countList = 3;
        int y = height / 2 - -24;
        for (DimensionType dimensionType : dimensionIds) {
            String dimensionName = dimensionType.getName();
            guiButtonList.add(new GuiButton(count++, width / 2 - (x - 20), y, 65, 10, dimensionName));
            if (guiButtonList.get(countList).x >= 320) {
                y = height / 2 - -42;
                x = 293;
            }
            countList++;
            x -= 70;
        }
        for (GuiButton button : guiButtonList) {
            buttonList.add(button);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int guiLeft = 500;
        int guiTop = height - 40;
        int guiRight = width - 500;
        int guiBottom = 15;
        drawRect(guiLeft, guiTop, guiRight, guiBottom, 0xFF000000);
        fontRenderer.drawString("Configuracoes Kaia", (width - fontRenderer.getStringWidth("Configurações Kaia")) / 2, height - 261, Color.WHITE.getRGB());
        fontRenderer.drawString("Area de mineração", width / 2 - 200, height / 2 - 85, Color.WHITE.getRGB());
        fontRenderer.drawString("Alcance do ataque", width / 2 - 200, height / 2 - 60, Color.WHITE.getRGB());
        fontRenderer.drawString("Atacar entidades amigaveis", width / 2 - 200, height / 2 - 35, Color.WHITE.getRGB());
        fontRenderer.drawString("Atacar todas as entidades", width / 2 - 200, height / 2 - 15, Color.WHITE.getRGB());
        fontRenderer.drawString("teletransporta xyz ", width / 2 - 200, height / 2 - -5, Color.WHITE.getRGB());
        if (guiTextFieldList.get(0) != null) {
            guiTextFieldList.get(0).drawTextBox();
        }
        if (guiTextFieldList.get(1) != null) {
            guiTextFieldList.get(1).drawTextBox();
        }
        if (guiTextFieldList.get(2) != null) {
            guiTextFieldList.get(2).setText(String.format("%.0f",player.posX));
            guiTextFieldList.get(2).drawTextBox();
        }
        if (guiTextFieldList.get(3) != null) {
            guiTextFieldList.get(3).setText(String.format("%.0f",player.posY));
            guiTextFieldList.get(3).drawTextBox();
        }
        if (guiTextFieldList.get(4) != null) {
            guiTextFieldList.get(4).setText(String.format("%.0f",player.posZ));
            guiTextFieldList.get(4).drawTextBox();
        }
        List<String> textButtonList = new ArrayList<>();
        textButtonList.add(String.valueOf(player.getHeldItemMainhand().getTagCompound().getBoolean("killFriendEntities")));
        textButtonList.add(String.valueOf(player.getHeldItemMainhand().getTagCompound().getBoolean("killAllEntities")));
        textButtonList.add("concluir");
        Integer[] dimensionIds = DimensionManager.getIDs();
        for (int dimensionId : dimensionIds) {
            DimensionType dimensionType = DimensionManager.getProviderType(dimensionId);
            textButtonList.add(dimensionType.getName());
        }
        int x = 0;
        for (GuiButton guiButton : guiButtonList) {
            if (guiButton != null) {
                guiButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
                if (x < textButtonList.size()) {
                    guiButton.displayString = textButtonList.get(x);
                    x++;
                }
                if (x >= 3) {
                    DimensionType[] dimensionTpHash = DimensionType.values();
                    for (DimensionType dimensionType : dimensionTpHash) {
                        dimensionHash.put(guiButton, dimensionType.getId());
                    }
                }
            }
        }

//        if (guiButtonList.get(0) != null) {
//            guiButtonList.get(0).drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
//            guiButtonList.get(0).displayString = String.valueOf(player.getHeldItemMainhand().getTagCompound().getBoolean("killFriendEntities"));
//        }
//        if (guiButtonList.get(1) != null) {
//            guiButtonList.get(1).displayString = String.valueOf(player.getHeldItemMainhand().getTagCompound().getBoolean("killAllEntities"));
//            guiButtonList.get(1).drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
//        }
//        if (guiButtonList.get(2) != null) {
//            guiButtonList.get(2).displayString = "concluir";
//            guiButtonList.get(2).drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
//        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (guiTextFieldList.get(0) != null) {
            guiTextFieldList.get(0).mouseClicked(mouseX, mouseY, button);
        }
        if (guiTextFieldList.get(1) != null) {
            guiTextFieldList.get(1).mouseClicked(mouseX, mouseY, button);
        }
        if (guiTextFieldList.get(2) != null) {
            guiTextFieldList.get(2).mouseClicked(mouseX, mouseY, button);
        }
        if (guiTextFieldList.get(3) != null) {
            guiTextFieldList.get(3).mouseClicked(mouseX, mouseY, button);
        }
        if (guiTextFieldList.get(4) != null) {
            guiTextFieldList.get(4).mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (guiTextFieldList.get(0).isFocused()) {
            guiTextFieldList.get(0).textboxKeyTyped(typedChar, keyCode);
        }
        if (guiTextFieldList.get(1).isFocused()) {
            guiTextFieldList.get(1).textboxKeyTyped(typedChar, keyCode);
        }
        if (guiTextFieldList.get(2).isFocused()) {
            guiTextFieldList.get(2).textboxKeyTyped(typedChar, keyCode);
        }
        if (guiTextFieldList.get(3).isFocused()) {
            guiTextFieldList.get(3).textboxKeyTyped(typedChar, keyCode);
        }
        if (guiTextFieldList.get(4).isFocused()) {
            guiTextFieldList.get(4).textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (guiTextFieldList.get(0).getText().matches("[0-9]+")) {
            this.areaBloco = Integer.valueOf(guiTextFieldList.get(0).getText());
            NetworkRegister.ACESS.sendToServer(new AreaBlocoPacket(this.areaBloco));
        }
        if (guiTextFieldList.get(1).getText().matches("[0-9]+")) {
            int rangeAttack = Integer.valueOf(guiTextFieldList.get(1).getText());
            NetworkRegister.ACESS.sendToServer(new KillRangeAttack(rangeAttack));
        }
    }

    @Override
    public void drawBackground(int tint) {
    }

    int dimensionId = 0;

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                boolean value = player.getHeldItemMainhand().getTagCompound().getBoolean("killFriendEntities");
                NetworkRegister.ACESS.sendToServer(new KillFriendEntitiesPacket(!value));
                break;
            case 1:
                value = player.getHeldItemMainhand().getTagCompound().getBoolean("killAllEntities");
                NetworkRegister.ACESS.sendToServer(new KillAllEntitiesPacket(!value));
                break;
            case 2:
                if (guiTextFieldList.get(2).getText().matches("[0-9]+") && guiTextFieldList.get(3).getText().matches("[0-9]+") && guiTextFieldList.get(4).getText().matches("[0-9]+")) {
                    double x = Double.parseDouble(guiTextFieldList.get(2).getText());
                    double y = Double.parseDouble(guiTextFieldList.get(3).getText());
                    double z = Double.parseDouble(guiTextFieldList.get(4).getText());
                    player.setPosition(x, y, z);
                    NetworkRegister.ACESS.sendToServer(new ChangeDimensionPacket(dimensionId));
                }
                break;
        }
        for (int c = 0; c < guiButtonList.size(); c++) {
            if (button.id == c) {
                dimensionId = dimensionHash.get(button);
            }
        }
    }
}
