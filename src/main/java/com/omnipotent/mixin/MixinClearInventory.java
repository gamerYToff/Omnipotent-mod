package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.command.CommandException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public abstract class MixinClearInventory implements IInventory {


    @Shadow public EntityPlayer player;
    @Inject(method = "clearMatchingItems", at = @At("HEAD"))
    public void execute(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT, CallbackInfoReturnable<Integer> cir){
        if(KaiaUtil.hasInInventoryKaia(player) ) {
            cir.cancel();
        }
    }
}
