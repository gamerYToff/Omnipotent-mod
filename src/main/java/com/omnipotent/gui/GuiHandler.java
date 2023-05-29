package com.omnipotent.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public enum GuiIDs {
        ID_MOD
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (GuiIDs.values()[ID]) {
            case ID_MOD:
                return new KaiaGui(player.inventory, player.getHeldItemMainhand());
        }
        throw new IllegalArgumentException("sem gui com o id" + ID);
    }
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (GuiIDs.values()[ID]) {
            case ID_MOD:
                return new KaiaGui(player.inventory, player.getHeldItemMainhand());
        }
        throw new IllegalArgumentException("sem gui com o id" + ID);
    }
}
