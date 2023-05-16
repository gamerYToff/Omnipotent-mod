package com.omnipotent.tools;

import net.minecraft.inventory.InventoryBasic;

public class KaiaInventory extends InventoryBasic {
    public KaiaInventory() {
        super("KaiaInventory", false, 9);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
}