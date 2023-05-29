package com.omnipotent.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class KaiaContainer extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
